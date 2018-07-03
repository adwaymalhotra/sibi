package com.sibi.authentication.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.sibi.authentication._interface.IRegistrationView;
import com.sibi.model.User;
import com.sibi.util.firebase.CloudInteractor;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by adway on 28/11/17.
 */

public class RegistrationPresenter {
    public static final String TAG = "RegistrationPresenter";
    private IRegistrationView view;
    private CloudInteractor cloudInteractor;
    private Bitmap selectedBitmap;
    private File file;

    @Inject public RegistrationPresenter(CloudInteractor cloudInteractor) {
        this.cloudInteractor = cloudInteractor;
    }

    public void setSelectedBitmap(Bitmap selectedBitmap) {
        this.selectedBitmap = selectedBitmap;
    }

    public void setView(IRegistrationView view) {
        this.view = view;
    }

    private void uploadImage(Bitmap bitmap) {

    }

    public void doRegistration() {
        String email = view.getEmail();
        String pwd = view.getPassword();
        String name = view.getName();

        User user = new User();
        user.setEmail(email);
        user.setName(name);

        cloudInteractor.createUser(email, pwd, objects -> {
            if (objects.length > 0) {
                if (objects[0] instanceof Exception) {
                    view.showToast("Registration Failed! Please check your credentials," +
                        " or try again later");
                    ((Exception) objects[0]).printStackTrace();
                }
            } else {
                cloudInteractor.setUserData(selectedBitmap, name, result -> {
                    if (((boolean) result[0])) {
                        Log.d(TAG, "doRegistration: user data updated successfully.");
                        view.showToast("Success!");
                        view.redirectToLogin();
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        view.showToast((String) result[1]);
                    }
                });
            }
        });
    }
}
