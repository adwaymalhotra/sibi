package com.sibi.primary.drawer.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.sibi.model.Category;
import com.sibi.model.PhotoDTO;
import com.sibi.model.User;
import com.sibi.primary.drawer._interface.INavDrawerView;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.firebase.CloudInteractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by adway on 03/12/17.
 */

public class NavDrawerPresenter {
    private final PersistenceInteractor persistenceInteractor;
    private final CloudInteractor cloudInteractor;
    private User user;
    private INavDrawerView view;
    private List<Category> categories = new ArrayList<>();

    @Inject
    public NavDrawerPresenter(CloudInteractor cloudInteractor, PersistenceInteractor persistenceInteractor) {
        this.cloudInteractor = cloudInteractor;
        updateUserData();
        this.persistenceInteractor = persistenceInteractor;
//        updateCategories();
    }

    public void setView(INavDrawerView view) {
        this.view = view;
    }

    private void updateCategories() {
        categories = user.getCategories();
    }

    public Map<Category, Double> getCategorySpendingMap() {
        return persistenceInteractor.getCategorySpendingMap(user.getCategories());
    }

    public String getName() {
        return user.getName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getImageUrl() {
        PhotoDTO dto = new Gson().fromJson(user.getJson(), PhotoDTO.class);
        return dto.cloudPath;
    }

    public void signOut() {
        view.getGoogleSignInClient().signOut().addOnCompleteListener(runnable -> {
            FirebaseAuth.getInstance().signOut();
            persistenceInteractor.clear();
            view.gotoSplash();
        });
    }

    public void updateUserData() {
        this.user = cloudInteractor.getUserData();
        if (view != null) view.updateCategoryList();
    }
}
