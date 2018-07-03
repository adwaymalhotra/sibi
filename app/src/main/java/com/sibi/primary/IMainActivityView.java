package com.sibi.primary;


import android.support.v4.app.Fragment;
import android.view.View;

import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.primary.drawer._interface.INavDrawerView;
import com.sibi.util.ICallback;

import java.util.List;

/**
 * Created by adway on 02/12/17.
 */

public interface IMainActivityView {
    void showNavDrawer(boolean b);
    void gotoDashboard();
    void gotoDashboard(View titleView, String name);
    void gotoAddExpense1();
    void gotoAddExpense1(View view, String string);
    void gotoAddExpense2();
    void gotoAddExpense2(View view, String string);
    void gotoAddExpense3();
    void gotoAddExpense3(View view, String string);
    void gotoEditTransaction(String transactionKey);
    void gotoEditTransaction(View view, String transactionKey);
    void gotoEditCategory(Category category);
    void loadFragment(Fragment frag, String tag);
    void setTitle(String title, boolean showNavDrawer);
    void gotoReviewTransactions();
    void gotoReviewTransactions(View view, String string);
    void gotoCategoryView(Category category);
    void updateAddExpense(View view, double v);
    void updateAddExpenseName(String s);
    void updateAddExpenseName(String s, View view);
    List<Category> getCategories();
    void showToast(String message);
    void finalizeExpense(String category, long time, ICallback callback);
    String getAmountPaidText();
    String getReceiverText();
    Transaction getTransaction();
    void clearBackStack();
    void closeNavDrawer();
    void finishFragment();
    void setCurrentFragment(String currentFragment);
    void updateNavDrawer();
    void setNavDrawer(INavDrawerView navDrawer);
    void gotoManageBudget();

    void gotoWelcome();
}
