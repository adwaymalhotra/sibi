package com.sibi.primary;

import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.model.User;
import com.sibi.util.ICallback;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.firebase.CloudInteractor;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by adway on 02/12/17.
 */

public class MainActivityPresenter {
    private final PersistenceInteractor persistence;
    private User user;
    private CloudInteractor cloudInteractor;
    private Transaction transaction = new Transaction();
    private IMainActivityView view;

    @Inject
    MainActivityPresenter(CloudInteractor cloudInteractor, PersistenceInteractor persistence) {
        this.cloudInteractor = cloudInteractor;
        this.persistence = persistence;
        this.user = cloudInteractor.getUserData();

    }

    public void setView(IMainActivityView view) {
        this.view = view;
        if(persistence.getFirstRun()) {
            view.gotoWelcome();
        }
    }

    void initNewExpense() {
        transaction = new Transaction();
        transaction.setUserEmail(user.getEmail());
        transaction.setType(Transaction.TYPE_EXPENSE);
    }

    void updateAddExpense(double amount) {
        transaction.setAmount(amount);
    }

    void updateAddExpenseName(String name) {
        transaction.setName(name);
    }

    List<Category> getCategories() {
        return user.getCategories();
    }

    void finalizeExpense(String category, long time, ICallback callback) {
        transaction.setCategory(category);
        transaction.setTimestamp(time);
        transaction.setLatestUpdateTimestamp(System.currentTimeMillis());
        cloudInteractor.addTransaction(transaction, callback);
    }

    String getAmountPaidText() {
        return "Amount Paid: $" + new DecimalFormat("#.##").format(transaction.getAmount());
    }

    String getReceiverText() {
        return "Receiver: " + transaction.getName();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    void setCurrentFragment(String currentFragment) {
        persistence.saveCurrentFragment(currentFragment);
    }

    String getLatestFrag() {
        return persistence.getLatestFragment();
    }

    Category getLastCategory() {
        String lastCategory = persistence.getLastCategory();
        if (lastCategory.isEmpty()) return null;
        final Category[] category = new Category[1];
        user.getCategories().stream()
            .filter(c -> c.getName().equals(lastCategory)).findFirst().ifPresent(c -> category[0] = c);
        return category[0];
    }
}
