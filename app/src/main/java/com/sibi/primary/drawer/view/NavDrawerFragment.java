package com.sibi.primary.drawer.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.sibi.R;
import com.sibi.Sibi;
import com.sibi.SplashActivity;
import com.sibi.model.Category;
import com.sibi.primary.IMainActivityView;
import com.sibi.primary.drawer._interface.INavDrawerView;
import com.sibi.primary.drawer.presenter.NavDrawerPresenter;
import com.sibi.primary.reviewTransactions.view.RecyclerItemClickListener;
import com.sibi.util.Constants;
import com.sibi.util.dagger.AppComponentProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by adway on 03/12/17.
 */

public class NavDrawerFragment extends Fragment
    implements INavDrawerView, Sibi.UserUpdateListener, RecyclerItemClickListener {
    public static final String TAG = "NavDrawerFragment";

    @BindView(R.id.navdrawer_rv) RecyclerView recyclerView;
    @BindView(R.id.user_name) TextView nameTV;
    @BindView(R.id.user_email) TextView emailTV;
    @BindView(R.id.user_img) ImageView imageIV;
    @BindView(R.id.dashboard_btn) View dashboard;
    @BindView(R.id.nav_fab) FloatingActionButton fab;

    @Inject NavDrawerPresenter presenter;
    private IMainActivityView parentView;
    private CategoryAdapter adapter;
    private boolean longPressToast = false;

    private void initRecycler() {
        Map<Category, Double> map = presenter.getCategorySpendingMap();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(new ArrayList<>(map.keySet()),
            new ArrayList<>(map.values()), this);
        recyclerView.setAdapter(adapter);
    }

    private void initUserDetails() {
        nameTV.setText(presenter.getName());
        emailTV.setText(presenter.getEmail());
        if (presenter.getImageUrl() != null && !presenter.getImageUrl().isEmpty())
            Picasso.with(getContext())
                .load(presenter.getImageUrl()).into(imageIV);
    }

    @Override public void onItemClick(View view, int position) {
        parentView.gotoCategoryView(adapter.getCategory(position));
        parentView.closeNavDrawer();
        adapter.setSelected(-1);
        setFabAdd();
    }

    @Override public void onItemLongClick(View view, int position) {
        if (!longPressToast) Toast.makeText(getContext(),
            "Long Press to select each Category.", Toast.LENGTH_SHORT).show();
        adapter.setSelected(position);
//        if (adapter.getSelectedCategories().size() > 0) setFabDelete();
//        else setFabAdd();
    }

    private void setFabAdd() {
        fab.setBackgroundTintList(ColorStateList
            .valueOf(ContextCompat.getColor(getContext(), R.color.white)));
        fab.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
        fab.setImageResource(R.drawable.ic_add_accent_24dp);
    }

    private void setFabDelete() {
        fab.setBackgroundTintList(ColorStateList
            .valueOf(ContextCompat.getColor(getContext(), R.color.red)));
        fab.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        fab.setImageResource(R.drawable.ic_delete_black_24dp);
    }

    @OnClick(R.id.dashboard_btn) public void onDashboardClick() {
        parentView.gotoDashboard(dashboard, "name");
        parentView.closeNavDrawer();
    }

    @OnClick(R.id.manage_budgets_btn) public void onManageBudgets() {
        parentView.gotoManageBudget();
        parentView.closeNavDrawer();
    }

    @OnClick(R.id.sign_out_btn) public void onSignOut() {
        presenter.signOut();
    }

    @OnClick(R.id.nav_fab) public void onFABClick() {
        parentView.gotoEditCategory(null);
        parentView.closeNavDrawer();
    }

    @Override public void onUserUpdated() {
        initUserDetails();
    }

    @SuppressWarnings("ConstantConditions") @Override public int getColorAccent() {
        return ContextCompat.getColor(getContext(), R.color.colorAccent);
    }

    @Override public void gotoSplash() {
        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override public GoogleSignInClient getGoogleSignInClient() {
        return GoogleSignIn.getClient(getContext(),
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.Auth.GOOGLE_AUTH_ID).requestEmail().build());
    }

    @Override public void updateDrawer() {
        presenter.updateUserData();
    }

    @Override public void updateCategoryList() {
        adapter.updateCategories(new ArrayList<>(presenter.getCategorySpendingMap().keySet()));
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        parentView = (IMainActivityView) context;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, v);
        AppComponentProvider.getAppComponent().inject(this);
        ((Sibi) getActivity().getApplication()).setUserUpdateListener(this);
        presenter.setView(this);
        return v;
    }

    @Override public void onStart() {
        super.onStart();
        initUserDetails();
        initRecycler();
        parentView.setNavDrawer(this);
    }
}
