package com.sibi.primary.dashboard.presenter;

import android.util.Log;

import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.model.User;
import com.sibi.primary.dashboard._interface.IDashboardView;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.Utils;
import com.sibi.util.firebase.CloudInteractor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by adway on 02/12/17.
 */

public class DashboardPresenter {
    public static final String TAG = "DashboardPresenter";
    private final PersistenceInteractor persistenceInteractor;
    private final Disposable subscription;
    private User user;
    private IDashboardView view;

    @Inject
    public DashboardPresenter(CloudInteractor cloudInteractor, PersistenceInteractor persistenceInteractor) {
        this.user = cloudInteractor.getUserData();
        this.persistenceInteractor = persistenceInteractor;
        this.subscription = persistenceInteractor.getObservable().subscribe(o -> {
            Log.d(TAG, "DashboardPresenter: emitted: " + o);
            updateData();
        });
    }

    public void setView(IDashboardView view) {
        this.view = view;
    }

    public void updateData() {
        Map<String, Double> catTransMap = persistenceInteractor.getCategoryStringSpendingMap(user
            .getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        view.updateChart(catTransMap);
        // TODO: calculate wishlist status

        //monthly spending
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long start = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long end = cal.getTimeInMillis();

        double spending = persistenceInteractor.calculateSpendingForMonth(start, end);
        view.setBudgetStatus(user.getMonthlyBudget(), spending,
            user.getMonthlyBudget() - spending);
    }

    public int getColor(String cat) {
        final int[] code = {-1};
        user.getCategories().stream().filter(category -> category.getName().equals(cat))
            .findFirst().ifPresent(category -> code[0] = category.getColorCode());
        Log.d(TAG, "getColor: " + code[0]);
        return code[0];
    }

    public void unsubscribe() {
        subscription.dispose();
    }

    public void backupToJson() {
        Observable.fromCallable(() ->
            Utils.writeTransactionsToJson(persistenceInteractor.getTransactionsJson()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(file -> {
                // TODO: offer to email of backup to GDrive.
                Log.d(TAG, "backupToJson: file written to " + file.getPath());
                view.showToast("Backup file " + file.getName() + " written to storage.");
            }, Throwable::printStackTrace);
    }

    public void parseJson(URI uri) {
        Log.d(TAG, "parseJson: ");
        Observable.fromCallable(() -> {
            String json = Utils.loadTransactionsFromJson(uri);
            List<Transaction> transactions = new Gson().fromJson(json,
                new TypeToken<List<Transaction>>() {}.getType());
            if (transactions == null || transactions.size() < 1)
                throw new ParseException("File is either corrupt or empty.", 0);

            persistenceInteractor.storeTransactions(transactions);
            return true;
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(b -> {
                if (b) {
                    Log.d(TAG, "parseJson: successfully loaded file.");
                    view.showToast("Successfully loaded transactions.");
                } else {
                    Log.d(TAG, "parseJson: failed.");
                    view.showToast("Unable to parse selected file!");
                }
            }, Throwable::printStackTrace);
    }
}
