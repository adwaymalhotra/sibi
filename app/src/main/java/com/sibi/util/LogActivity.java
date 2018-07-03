package com.sibi.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.sibi.R;
import com.sibi.model.Transaction;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.util.firebase.CloudInteractor;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 27/11/17.
 */

public class LogActivity extends AppCompatActivity {
    public static final String TAG = "LogActivity";
    @BindView(R.id.tv) TextView tv;
    @Inject CloudInteractor cloudInteractor;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_log);
        ButterKnife.bind(this);
        AppComponentProvider.getAppComponent().inject(this);
    }

    @Override protected void onStart() {
        super.onStart();

        Transaction transaction = new Transaction();

        transaction.setAmount(11.22);
        transaction.setName("Test");
        transaction.setCategory("general");
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setType(Transaction.TYPE_EXPENSE);
        transaction.setLocationJson("sfds");
        transaction.setPhotoJson("photo");

        cloudInteractor.addTransaction(transaction, objects -> {
            if (((boolean) objects[0])) {
                append("transaction written to " + objects[1]);
                transaction.setKey((String) objects[1]);
                transaction.setName("Test_updated");
                transaction.setAmount(23);
                transaction.setUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                transaction.setLatestUpdateTimestamp(System.currentTimeMillis());

                cloudInteractor.updateTransaction(transaction, result -> {
                    if (((boolean) result[0])) {
//                        Transaction t = (Transaction) result[1];
                        append("update successful: ");
                    } else {
                        append((String) result[1]);
                    }
                });
            } else append("failed to create new transaction");
        });


    }

    private void append(String s) {
        Log.d(TAG, "append: " + s);
        s = tv.getText() + s + "\n";
        tv.setText(s);
    }
}
