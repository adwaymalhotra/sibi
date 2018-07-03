package com.sibi.primary.category.presenter;

import com.sibi.model.Category;
import com.sibi.model.User;
import com.sibi.primary.category._interface.IEditCategoryView;
import com.sibi.util.firebase.CloudInteractor;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by adway on 08/12/17.
 */

public class EditCategoryPresenter {
    private IEditCategoryView view;
    private CloudInteractor cloudInteractor;
    private User user;

    @Inject public EditCategoryPresenter(CloudInteractor cloudInteractor) {
        user = cloudInteractor.getUserData();
        this.cloudInteractor = cloudInteractor;
    }

    public void setView(IEditCategoryView view) {
        this.view = view;
    }

    public void saveTransaction() {
        String name = view.getName();
        int colorCode = view.getColorCode();

        List<Category> categories = user.getCategories();
        final Category[] ca = new Category[1];
        categories.stream().filter(c -> c.getName().equals(name)).findFirst().ifPresent(category -> {
            category.setName(name);
            category.setColorCode(colorCode);
            ca[0] = category;
        });

        if (ca[0] == null) {
            ca[0] = new Category();
            ca[0].setName(name);
            ca[0].setColorCode(colorCode);
            categories.add(ca[0]);
        }

        cloudInteractor.setUserData(user, objects -> {
            if (objects[0] instanceof Boolean) {
                if (((boolean) objects[0])) {
                    view.showToast("Success!");
                    view.finish();
                }
            } else {
                view.showToast("Something went wrong! Please try again.");
            }
        });
    }
}
