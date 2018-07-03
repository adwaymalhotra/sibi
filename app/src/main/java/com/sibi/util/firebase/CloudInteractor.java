package com.sibi.util.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.model.PhotoDTO;
import com.sibi.model.Transaction;
import com.sibi.model.User;
import com.sibi.model.Wish;
import com.sibi.util.Constants;
import com.sibi.util.ICallback;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.Utils;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by adway on 27/11/17.
 */

public class CloudInteractor {
    public static final String TAG = "CloudInteractor";
    private final PersistenceInteractor persistenceInteractor;
    private final Context context;
    public User user;

    public CloudInteractor(PersistenceInteractor persistenceInteractor, Context context) {
        this.persistenceInteractor = persistenceInteractor;
        this.context = context;
        getUserData(objects -> {});
    }

    public void addCategory(Category category) {
        // TODO: add category
    }

    public void updateCategory(Category category) {
        // TODO: update category and update all transactions
    }

    public void deleteCategory(Category category, Category replacement) {
        // TODO: delete category and update transactions to replacement
    }

    public void addTransaction(Transaction transaction, ICallback callback) {
        DatabaseReference db = FirebaseDatabase.getInstance()
            .getReference(String.format(Constants.Firebase.TRANSACTIONS,
                transaction.getUserEmail().replace(".", "_")));
        String key = db.push().getKey();
        transaction.setKey(key);
        updateNewTransactions();
        db.child(key).setValue(transaction).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onComplete(true, key);
            } else callback.onComplete(false);
        });
    }

    public void updateTransaction(Transaction transaction, ICallback callback) {
        transaction.setLatestUpdateTimestamp(System.currentTimeMillis());
        DatabaseReference db = FirebaseDatabase.getInstance()
            .getReference(String.format(Constants.Firebase.TRANSACTIONS,
                transaction.getUserEmail().replace(".", "_")))
            .child(transaction.getKey());
        db.setValue(transaction).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onComplete(true);
                updateNewTransactions();
            } else callback.onComplete(false, task.getException());
        });
    }

    public void deleteTransaction(Transaction transaction) {
        DatabaseReference db = FirebaseDatabase.getInstance()
            .getReference(String.format(Constants.Firebase.TRANSACTIONS,
                transaction.getUserEmail().replace(".", "_")));
        db.child(transaction.getKey()).removeValue((databaseError, databaseReference) -> {
            Log.d(TAG, "deleteTransaction: ");
            if (databaseError != null) {
                databaseError.toException().printStackTrace();
                Log.d(TAG, "deleteTransaction: deletion failed");
            } else {
                persistenceInteractor.removeValue(transaction);
            }
        });
    }

    public void addWishListItem(Wish wish) {
        //TODO addWishListItem()
    }

    public void doLogin(String email, String password, ICallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (isLoggedIn()) firebaseAuth.signOut();
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) callback.onComplete();
                else callback.onComplete(task.getException());
            });
    }

    public void doLoginViaGoogle(GoogleSignInAccount account, ICallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        if (isLoggedIn()) firebaseAuth.signOut();
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Observable.fromCallable(() -> BitmapFactory
                        .decodeStream(new URL(account.getPhotoUrl().toString()).openStream()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bitmap -> callback.onComplete(bitmap, account.getDisplayName()),
                            Throwable::printStackTrace);
//                        callback.onComplete(account.getPhotoUrl(),
//                                account.getDisplayName());
                } else callback.onComplete(task.getException());
            });
    }

    public void updateAllTransactions(ICallback callback) {
        String email = getCurrentUser().getEmail().replace(".", "_");
        DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference(String.format(Constants.Firebase.TRANSACTIONS, email));
        if (!isLoggedIn())
            callback.onComplete(false);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    List<Transaction> transactions = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        transactions.add(snapshot.getValue(Transaction.class));
                    }
                    persistenceInteractor.storeTransactions(transactions);
                    callback.onComplete(true);
                } else {
                    callback.onComplete("Nothing to update");
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(databaseError.getMessage());
            }
        });

    }

    public void updateNewTransactions() {
        updateNewTransactions(objects -> {
            if (objects[0] instanceof Boolean) {
                if ((boolean) objects[0]) {
                    Log.d(TAG, "updateNewTransactions: successful");
                }
            } else {
                Log.d(TAG, "updateNewTransactions: " + objects[0]);
            }
        });
    }

    public void updateNewTransactions(ICallback callback) {
        if (!isLoggedIn()) callback.onComplete(false);

        String email = getCurrentUser().getEmail().replace(".", "_");

        long timestamp = 0;
        List<Transaction> transactions = persistenceInteractor.getTransactions();
        if (transactions != null && transactions.size() > 0)
            timestamp = transactions.stream()
                .sorted((t0, t1) -> t0.getLatestUpdateTimestamp() > t1.getLatestUpdateTimestamp()
                    ? -1 : 1).collect(Collectors.toList()).get(0).getLatestUpdateTimestamp();
        Log.d(TAG, "updateNewTransactions: latest timestamp " + timestamp);

        Query q = FirebaseDatabase.getInstance()
            .getReference(String.format(Constants.Firebase.TRANSACTIONS, email))
            .orderByChild("latestUpdateTimestamp");
        q.startAt(timestamp + 1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() > 0) {
                    List<Transaction> transactions = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        transactions.add(snapshot.getValue(Transaction.class));
                    }
                    persistenceInteractor.addTransactions(transactions);
                    callback.onComplete(true);
                } else {
                    Log.d(TAG, "onDataChange: ");
                    callback.onComplete("Nothing to update");
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                callback.onComplete(databaseError.getMessage());
            }
        });
    }

    public void getUserData(ICallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.Firebase.USER_DATA);
        FirebaseUser firebaseUser = getCurrentUser();

        if (firebaseUser == null) {
            callback.onComplete("Not Logged In.");
            return;
        }
        ref.orderByChild("email").equalTo(getCurrentUser().getEmail())
            .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    CloudInteractor.this.user = user;
                    callback.onComplete(user);
                } else {
                    Log.d(TAG, "onDataChange: no results");
                    callback.onComplete("");
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled");
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                callback.onComplete("error");
            }
        });
    }

    public void updateBudget(Float budget, ICallback callback) {
        user.setMonthlyBudget(budget);
        setUserData(user, objects -> {
            if (objects[0] instanceof Boolean) {
                if (((boolean) objects[0])) {
                    callback.onComplete(true);
                    Log.d(TAG, "updateBudget: success");
                }
            } else {
                Log.d(TAG, "updateBudget: error updating budget");
            }
        });
    }

    public void setUserData(Bitmap bitmap, String name, ICallback callback) {
        StorageReference storageReference = FirebaseStorage.getInstance()
            .getReference();
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
            .getReference(Constants.Firebase.USER_DATA);
        dbRef.orderByChild("email").equalTo(getCurrentUser().getEmail())
            .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0) {
                    Log.d(TAG, "onDataChange: making new user");
                    String key = dbRef.push().getKey();
                    String path = "profile/" + key + ".jpg";
                    if (bitmap != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                        storageReference.child(path).putBytes(stream.toByteArray())
                            .addOnCompleteListener(task -> {
                                PhotoDTO dto = new PhotoDTO();
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "onDataChange: failed to upload image");
                                    callback.onComplete(false, "Failed to upload image");
                                } else dto.cloudPath = task.getResult().getDownloadUrl().toString();

                                Log.d(TAG, "setUserData: " + task.getResult().getDownloadUrl());

                                User user = getNewUser(name, dto);

                                dbRef.child(key).setValue(user).addOnCompleteListener(runnable -> {
                                    if (runnable.isSuccessful())
                                        callback.onComplete(true);
                                    else
                                        callback.onComplete(false,
                                            "Error while updating user data.");
                                });
                            });
                    } else {
                        User user = getNewUser(name, new PhotoDTO());
                        dbRef.child(key).setValue(user).addOnCompleteListener(runnable -> {
                            if (runnable.isSuccessful())
                                callback.onComplete(true);
                            else
                                callback.onComplete(false,
                                    "Error while updating user data.");
                        });
                    }
                } else {
                    Log.d(TAG, "onDataChange: existing user");
                    callback.onComplete(true);
                }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled user check");
            }
        });
    }

    private User getNewUser(String name, PhotoDTO dto) {
        User user = new User();
        user.setName(name);
        user.setEmail(getCurrentUser().getEmail());
        user.setJson(new Gson().toJson(dto));
        user.setMonthlyBudget(600);

        //default categories
        Category category = new Category();
        category.setColorCode(Utils.getColor(context, R.color.colorAccent));
        user.getCategories().add(category);
        category = new Category();
        category.setName("Food & Drink");
        category.setColorCode(Utils.getColor(context, R.color.red));
        user.getCategories().add(category);
        category = new Category();
        category.setName("Groceries");
        category.setColorCode(Utils.getColor(context, R.color.magenta));
        user.getCategories().add(category);
        category = new Category();
        category.setName("Savings");
        category.setColorCode(Utils.getColor(context, R.color.green));
        user.getCategories().add(category);
        return user;
    }

    public void createUser(String email, String pwd, ICallback callback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) callback.onComplete();
                else callback.onComplete(task.getException());
            });
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean isLoggedIn() {
//        getUserData(objects -> {});
        return getCurrentUser() != null;
    }

    public User getUserData() {
        return user;
    }

    public void setUserData(User user, ICallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
            .getReference(Constants.Firebase.USER_DATA);
        dbRef.orderByChild("email").equalTo(getCurrentUser().getEmail())
            .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getChildren().iterator().next().getKey();
                dbRef.child(key).setValue(user).addOnCompleteListener(runnable -> {
                    if (runnable.isSuccessful()) {
                        getUserData(objects -> {});
                        CloudInteractor.this.user = user;
                        callback.onComplete(true);
                    } else
                        callback.onComplete("Error while updating user data.");
                });
            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }
}