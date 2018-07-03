package com.sibi.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.primary.dashboard.view.DashboardFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by adway on 27/11/17.
 */

public class PersistenceInteractor {
    public static final String TAG = "PersistenceInteractor";
    private final SharedPreferences sharedPref;
    private IPersistenceUpdatedListener listener;
    private Observable<String> observable;

    public PersistenceInteractor(SharedPreferences defaultSharedPreferences) {
        this.sharedPref = defaultSharedPreferences;
        observable = Observable.create((ObservableEmitter<String> e)
            -> listener = () -> e.onNext("transactions")).share();
    }

    public Observable<String> getObservable() {
        return observable.observeOn(AndroidSchedulers.mainThread());
    }

    public double calculateSpendingForMonth(long start, long end) {
        double sum = 0.0;
        List<Transaction> transactions = getTransactions();

        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= start
                && transaction.getTimestamp() <= end) {
                double amount = transaction.getAmount();
                sum += amount;
            }
        }
        return sum;
    }

    public Map<String, Double> getCategoryStringSpendingMap(List<String> categories) {
        HashMap<String, Double> map = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long start = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long end = cal.getTimeInMillis();
        Log.d(TAG, "getCategoryStringSpendingMap: " + start + " " + end);
        List<Transaction> transactions = getTransactions().stream()
            .filter(t -> {
                Log.d(TAG, "getCategoryStringSpendingMap: " + t.getTimestamp());
                return t.getTimestamp() <= end && t.getTimestamp() >= start;
            }).collect(Collectors.toList());
        Log.d(TAG, "getCategoryStringSpendingMap: " + transactions.size());
        for (String c : categories) {
            map.put(c, transactions.stream().filter(t -> t.getCategory().equals(c))
                .mapToDouble(Transaction::getAmount).sum());
        }

//        transactions.stream().forEach(transaction -> {
//            categories.stream().filter(category ->
//                category.equals(transaction.getCategory()))
//                .findFirst().ifPresent(category -> {
//                map.put(category, map.get(category) + transaction.getAmount());
//            });
//        });
        return map;
    }

    public Map<Category, Double> getCategorySpendingMap(List<Category> categories) {
        HashMap<Category, Double> map = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long start = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long end = cal.getTimeInMillis();

        List<Transaction> transactions = getTransactions().stream()
            .filter(t -> t.getTimestamp() <= end && t.getTimestamp() >= start)
            .collect(Collectors.toList());

        for (Category c : categories) {
            map.put(c, transactions.stream().filter(t -> t.getCategory().equals(c.getName()))
                .mapToDouble(Transaction::getAmount).sum());
        }

//        transactions.forEach(transaction -> {
//            categories.stream().filter(category ->
//                category.getName().equals(transaction.getCategory()))
//                .findFirst().ifPresent(category -> {
//                map.put(category, map.get(category) + transaction.getAmount());
//            });
//        });
        return map;
    }

    public void addTransaction(Transaction transaction) {
        Observable.fromCallable(() -> {
            List<Transaction> all = getTransactions();
            all.add(transaction);
            storeTransactions(all);
            return true;
        }).subscribeOn(Schedulers.computation()).subscribe(aBoolean -> {
            if (aBoolean) Log.d(TAG, "addTransaction: added transaction "
                + transaction.getKey() + " to persistence");
        }, Throwable::printStackTrace, () -> listener.onTransactionsUpdated());
    }

    public void addTransactions(List<Transaction> transactions) {
        Log.d(TAG, "addTransactions: " + transactions.get(0));
        Observable.fromCallable(() -> {
            List<Transaction> all = getTransactions();
            for (Transaction transaction : transactions) {
                all.removeAll(all.stream().filter(t -> t.getKey()
                    .equals(transaction.getKey()))
                    .collect(Collectors.toList()));
            }
            all.addAll(transactions);
            storeTransactions(all);
            return true;
        }).subscribeOn(Schedulers.computation())
            .subscribe(aBoolean -> {
                if (aBoolean) Log.d(TAG, "addTransaction: added "
                    + transactions.size() + " transactions to persistence");
            }, Throwable::printStackTrace, () -> listener.onTransactionsUpdated());
    }

    public void storeTransactions(List<Transaction> transactions) {
        sharedPref.edit().putString(Constants.Keys.KEY_TRANSACTIONS,
            new Gson().toJson(transactions)).apply();
        Log.d(TAG, "storeTransactions: stored "
            + transactions.size() + " transactions to persistence.");
//        listener.onTransactionsUpdated();
    }

    public List<Transaction> getTransactions() {
        String json = sharedPref.getString(Constants.Keys.KEY_TRANSACTIONS, "");
        if (json.isEmpty()) return new ArrayList<>();
        else {
            TypeToken token = new TypeToken<List<Transaction>>() {};
            return new Gson().fromJson(json, token.getType());
        }
    }

    public List<Transaction> getTransactions(String category) {
        List<Transaction> all = getTransactions();
        return all.stream()
            .filter(transaction -> transaction.getCategory().equals(category))
            .collect(Collectors.toList());
    }

    public void storeEmail(String email) {
        sharedPref.edit().putString(Constants.Auth.CURRENT_EMAIL, email).apply();
    }

    public String getEmail() {
        return sharedPref.getString(Constants.Auth.CURRENT_EMAIL, null);
    }

    public void updateTransaction(Transaction t) {
        Observable.fromCallable(() -> {
            List<Transaction> transactions = getTransactions();
            transactions.stream()
                .filter(t1 -> t1.getKey().equals(t.getKey()))
                .findFirst()
                .ifPresent(t1 -> t1 = t);
            storeTransactions(transactions);
            return true;
        }).subscribeOn(Schedulers.computation())
            .subscribe(aBoolean -> {
                if (aBoolean)
                    Log.d(TAG, "updateTransaction: transaction " + t.getKey() + " updated.");
            }, Throwable::printStackTrace, () -> listener.onTransactionsUpdated());
    }

    public void removeValue(Transaction transaction) {
        Observable.fromCallable(() -> {
            List<Transaction> all = getTransactions();
            all.removeAll(all.stream().filter(t -> t.getKey().equals(transaction.getKey()))
                .collect(Collectors.toList()));
            storeTransactions(all);
            return true;
        }).subscribeOn(Schedulers.computation())
            .subscribe(boo -> {
                if (boo) Log.d(TAG, "removeValue: deleted " + transaction.getKey());
            }, Throwable::printStackTrace, () -> listener.onTransactionsUpdated());
    }

    public void clear() {
        sharedPref.edit().putString(Constants.Keys.KEY_TRANSACTIONS, "").apply();
        sharedPref.edit().putString(Constants.Keys.KEY_CURRENT_FRAG, "").apply();
        sharedPref.edit().putBoolean("firstrun", true).apply();
    }

    public String getTransactionsJson() {
        return new Gson().toJson(getTransactions());
    }

    public void saveCurrentFragment(String currentFragment) {
        Log.d(TAG, "saveCurrentFragment: " + currentFragment);
        sharedPref.edit().putString(Constants.Keys.KEY_CURRENT_FRAG, currentFragment).apply();
    }

    public String getLatestFragment() {
        return sharedPref.getString(Constants.Keys.KEY_CURRENT_FRAG, DashboardFragment.TAG);
    }

    public void saveLastCategory(String category) {
        sharedPref.edit().putString(Constants.Keys.KEY_CATEGORY, category).apply();
    }

    public String getLastCategory() {
        return sharedPref.getString(Constants.Keys.KEY_CATEGORY, "");
    }

    public void setFirstRun() {
        sharedPref.edit().putBoolean("firstrun", false).apply();
    }


    public boolean getFirstRun() {
        return sharedPref.getBoolean("firstrun", true);
    }
}
