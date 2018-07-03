package com.sibi.primary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sibi.R;
import com.sibi.model.Category;
import com.sibi.model.Transaction;
import com.sibi.primary.addExpense.view.AddExpFragment1;
import com.sibi.primary.addExpense.view.AddExpFragment2;
import com.sibi.primary.addExpense.view.AddExpFragment3;
import com.sibi.primary.category.view.EditCategoryFragment;
import com.sibi.primary.dashboard.view.DashboardFragment;
import com.sibi.primary.drawer._interface.INavDrawerView;
import com.sibi.primary.drawer.view.NavDrawerFragment;
import com.sibi.primary.editTransaction.view.EditTransactionFragment;
import com.sibi.primary.reviewTransactions.view.CategoryFragment;
import com.sibi.primary.reviewTransactions.view.ReviewTransactionsFragment;
import com.sibi.util.Constants;
import com.sibi.util.ICallback;
import com.sibi.util.Utils;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.welcome.WelcomeActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by adway on 22/11/17.
 */

public class MainActivity extends AppCompatActivity implements IMainActivityView {
    public static final String TAG = "MainActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Inject MainActivityPresenter presenter;
    private INavDrawerView navDrawer;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initNavDrawer();
        String lastFrag = getIntent().getStringExtra("current_fragment");


        if (lastFrag != null) {
            reloadBackStack(lastFrag);
            getIntent().removeExtra("current_fragment");
        } else {
            lastFrag = presenter.getLatestFrag();
            reloadBackStack(lastFrag);
        }
    }

    private void reloadBackStack(String lastFrag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(lastFrag);
        switch (lastFrag) {
            case DashboardFragment.TAG:
                gotoDashboard();
                break;
            case CategoryFragment.TAG:
                if (fragment != null) loadFragment(fragment, lastFrag);
                else gotoCategoryView(presenter.getLastCategory());
                break;
            case ReviewTransactionsFragment.TAG:
                gotoDashboard();
                if (fragment != null) loadFragment(fragment, lastFrag);
                else gotoReviewTransactions();
                break;
            case AddExpFragment1.TAG:
                gotoDashboard();
                gotoAddExpense1();
                break;
            default:
                gotoDashboard();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private void initNavDrawer() {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_drawer, new NavDrawerFragment())
            .commit();
    }

    @Override public void showNavDrawer(boolean show) {
        if (show) {
//            drawerLayout.addDrawerListener(toggle);
//            toggle.setDrawerIndicatorEnabled(true);
//            toggle.syncState();
            toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
            toolbar.setNavigationOnClickListener(view -> {
                drawerLayout.openDrawer(Gravity.START);
            });
        } else {
//            toggle.setDrawerIndicatorEnabled(false);
//            toggle.
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(view -> {
                onBackPressed();
            });
        }
    }

    @Override public void gotoDashboard() {
        Fragment fragment = new DashboardFragment();
        presenter.setCurrentFragment(DashboardFragment.TAG);
        loadFragment(fragment, DashboardFragment.TAG);
    }

    @Override public void gotoDashboard(View titleView, String name) {
        Fragment fragment = new DashboardFragment();
        presenter.setCurrentFragment(DashboardFragment.TAG);
        loadFragment(fragment, DashboardFragment.TAG, titleView, name);
    }

    @Override public void gotoAddExpense1() {
        presenter.initNewExpense();
        Fragment f = getSupportFragmentManager().findFragmentByTag(AddExpFragment1.TAG);;
        if(f==null)
            f = new AddExpFragment1();
        loadFragment(f, AddExpFragment1.TAG);
    }

    @Override public void gotoAddExpense1(View view, String string) {

        presenter.initNewExpense();
        Fragment f = getSupportFragmentManager().findFragmentByTag(AddExpFragment1.TAG);;
        if(f==null)
            f = new AddExpFragment1();
        loadFragment(f, AddExpFragment1.TAG, view, string);
    }

    @Override public void gotoAddExpense2() {
        loadFragment(new AddExpFragment2(), AddExpFragment2.TAG);
    }

    @Override public void gotoAddExpense2(View view, String string) {
        loadFragment(new AddExpFragment2(), AddExpFragment2.TAG, view, string);
    }

    @Override public void gotoAddExpense3() {
        loadFragment(new AddExpFragment3(), AddExpFragment3.TAG);
    }

    @Override public void gotoAddExpense3(View view, String string) {
        loadFragment(new AddExpFragment3(), AddExpFragment3.TAG, view, string);
    }

    @Override public void gotoEditTransaction(String transactionKey) {
        Bundle b = new Bundle();
        b.putString(Constants.Keys.KEY_EDIT_TRANSACTION, transactionKey);
        Fragment f = new EditTransactionFragment();
        f.setArguments(b);
        loadFragment(f, EditTransactionFragment.TAG);
    }

    @Override public void gotoEditTransaction(View view, String transactionKey) {
        Bundle b = new Bundle();
        b.putString(Constants.Keys.KEY_EDIT_TRANSACTION, transactionKey);
        Fragment f = new EditTransactionFragment();
        f.setArguments(b);
        view.setTransitionName(getString(R.string.edit_trans));
        loadFragment(f, EditTransactionFragment.TAG, view, getString(R.string.edit_trans));
    }

    @Override public void gotoEditCategory(Category category) {
        Fragment f = new EditCategoryFragment();
        Bundle b = new Bundle();
        if (category != null) {
            b.putString(Constants.Keys.KEY_CAT_EDIT, "Edit Category");
            b.putString(Constants.Keys.KEY_CATEGORY, new Gson().toJson(category));
        }
        f.setArguments(b);
        loadFragment(f, EditCategoryFragment.TAG);
    }

    @Override public void loadFragment(Fragment fragment, String tag) {
        fragment.setEnterTransition(new Fade().setDuration(300));
        fragment.setExitTransition(new Fade().setDuration(300));
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction().replace(R.id.container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @SuppressWarnings("ConstantConditions")
    @Override public void setTitle(String title, boolean navDrawer) {
        setTitle(title);
        showNavDrawer(navDrawer);
    }

    @Override public void gotoReviewTransactions() {
        presenter.setCurrentFragment(ReviewTransactionsFragment.TAG);
        loadFragment(new ReviewTransactionsFragment(),
            ReviewTransactionsFragment.TAG);
    }

    @Override public void gotoReviewTransactions(View view, String string) {
        presenter.setCurrentFragment(ReviewTransactionsFragment.TAG);
        loadFragment(new ReviewTransactionsFragment(), ReviewTransactionsFragment.TAG,
            view, string);
    }

    @Override public void gotoCategoryView(Category category) {
        Fragment fragment = new CategoryFragment();
        if (category != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.Keys.KEY_CATEGORY, category.getName());
            fragment.setArguments(bundle);
        }
        presenter.setCurrentFragment(CategoryFragment.TAG);
        loadFragment(fragment, CategoryFragment.TAG);
    }

    @Override public void updateAddExpense(View view, double amount) {
        presenter.updateAddExpense(amount);
        gotoAddExpense2(view, getString(R.string.amount_text));
    }

    @Override public void updateAddExpenseName(String name) {
        presenter.updateAddExpenseName(name);
        gotoAddExpense3();
    }

    @Override public void updateAddExpenseName(String name, View view) {
        presenter.updateAddExpenseName(name);
        gotoAddExpense3(view, getString(R.string.enter_name));
    }

    @Override public List<Category> getCategories() {
        return presenter.getCategories();
    }

    @Override public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void finalizeExpense(String category, long time, ICallback callback) {
        presenter.finalizeExpense(category, time, callback);
    }

    @Override public String getAmountPaidText() {
        return presenter.getAmountPaidText();
    }

    @Override public String getReceiverText() {
        return presenter.getReceiverText();
    }

    @Override public Transaction getTransaction() {
        return presenter.getTransaction();
    }

    @Override public void clearBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() - 1; i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override public void closeNavDrawer() {
        drawerLayout.closeDrawer(Gravity.START);
    }

    @Override public void finishFragment() {
        onBackPressed();
    }

    @Override public void setCurrentFragment(String currentFragment) {
        presenter.setCurrentFragment(currentFragment);
    }

    @Override public void updateNavDrawer() {
        navDrawer.updateDrawer();
    }

    @Override public void setNavDrawer(INavDrawerView navDrawer) {
        this.navDrawer = navDrawer;
    }

    @Override public void gotoManageBudget() {
        loadFragment(new ManageBudgetFragment(), ManageBudgetFragment.TAG);
    }

    @Override public void gotoWelcome() {
        //does nothing until the UI is fixed.
//        Intent i = new Intent(this, WelcomeActivity.class);
//        startActivity(i);
    }

    private void loadFragment(Fragment fragment, String tag,
                              View titleView, String name) {
//        Fade transition = new Fade();
//        transition.setDuration(1000);
        fragment.setSharedElementEnterTransition(new Utils.FragmentTransition());
        fragment.setSharedElementReturnTransition(new Utils.FragmentTransition());
        fragment.setEnterTransition(new Fade().setDuration(300));
        fragment.setExitTransition(new Fade().setDuration(300));
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction().replace(R.id.container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.addSharedElement(titleView, name);
        transaction.commit();
    }

    @Override public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
        else
            finish();
    }
}