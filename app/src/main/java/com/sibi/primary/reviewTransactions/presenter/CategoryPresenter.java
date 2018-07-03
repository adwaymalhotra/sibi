package com.sibi.primary.reviewTransactions.presenter;

import android.util.Log;

import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.model.User;
import com.sibi.primary.reviewTransactions._interface.ICategoryView;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.firebase.CloudInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by adway on 03/12/17.
 */

public class CategoryPresenter {
    public static final String TAG = "CategoryPresenter";
    private final Disposable disposable;
    private User user;
    private CloudInteractor cloudInteractor;
    private PersistenceInteractor persistenceInteractor;
    private ICategoryView view;
    private String category = "";
    private List<Transaction> transactions = new ArrayList<>();
    private Category cat;

    @Inject
    public CategoryPresenter(PersistenceInteractor persistenceInteractor,
                             CloudInteractor cloudInteractor) {
        this.persistenceInteractor = persistenceInteractor;
        this.cloudInteractor = cloudInteractor;
        this.user = cloudInteractor.getUserData();
        this.disposable = persistenceInteractor.getObservable()
            .subscribe(s -> {
                Log.d(TAG, "CategoryPresenter: " + s);
                udpateData();
            });
    }

    public void setView(ICategoryView view) {
        this.view = view;
    }

    public void udpateData() {
        transactions.clear();
        this.user = cloudInteractor.getUserData();
        if (category.isEmpty()) {
            transactions.addAll(persistenceInteractor.getTransactions());
        } else {
            transactions.addAll(persistenceInteractor.getTransactions(category));
        }
        if (view != null)
            view.updateList();
    }

    public List<Transaction> getTransactionList() {
        return transactions;
    }

    public Category getCategory() {
        return cat;
    }

    public void setCategory(String category) {
        this.category = category;
        user.getCategories().stream().filter(c -> c.getName().equals(category)).findFirst()
            .ifPresent(category1 -> cat = category1);
        persistenceInteractor.saveLastCategory(category);

    }

    public void unsubscribe() {
        disposable.dispose();
    }
}
