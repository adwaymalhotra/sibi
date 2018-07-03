package com.sibi.primary.editTransaction.presenter;

import android.util.Log;

import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.model.User;
import com.sibi.primary.editTransaction._interface.IEditTransactionView;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.firebase.CloudInteractor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by adway on 05/12/17.
 */

public class EditTransactionPresenter {
    public static final String TAG = "EditTransactionPresente";
    private final CloudInteractor cloudInteractor;
    private IEditTransactionView view;
    private Transaction transaction;
    private User user;
    private PersistenceInteractor persistenceInteractor;

    @Inject
    public EditTransactionPresenter(PersistenceInteractor persistenceInteractor,
                                    CloudInteractor cloudInteractor) {
        this.persistenceInteractor = persistenceInteractor;
        this.user = cloudInteractor.getUserData();
        this.cloudInteractor = cloudInteractor;
    }

    public void setView(IEditTransactionView view) {
        this.view = view;
    }

    public void initTransactionObject(String key) {
        persistenceInteractor.getTransactions().stream().filter(t -> t.getKey().equals(key))
            .findFirst().ifPresent(t -> transaction = t);

        view.updateData();
    }

    public String getName() {
        return transaction.getName();
    }

    public String getCategory() {
        return transaction.getCategory();
    }

    public String getAmountText() {
        return new DecimalFormat("#.00").format(transaction.getAmount());
    }

    public String getDate() {
        return new SimpleDateFormat("MMM dd, yyyy", Locale.US)
            .format(new Date(transaction.getTimestamp()));
    }

    public List<Category> getCategories() {
        return user.getCategories();
    }

    public void saveTransaction() {
        transaction.setName(view.getName());
        transaction.setAmount(view.getAmount());
        transaction.setTimestamp(view.getTimestamp());
        transaction.setCategory(view.getCategory());
        cloudInteractor.updateTransaction(transaction, result -> {
            if (((boolean) result[0])) {
                Log.d(TAG, "saveTransaction: success");
                view.onUpdateSuccess();
            } else {
                Log.d(TAG, "saveTransaction: " + result[1]);
                view.onUpdateFailure();
            }
        });
    }

    public void deleteTransaction() {
        cloudInteractor.deleteTransaction(transaction);
        view.onDelete();
    }

    public long getTime() {
        return transaction.getTimestamp();
    }
}
