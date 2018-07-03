package com.sibi.authentication.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.sibi.authentication._interface.ILoginView;
import com.sibi.model.User;
import com.sibi.util.Constants;
import com.sibi.util.firebase.CloudInteractor;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import javax.inject.Inject;

/**
 * Created by adway on 28/11/17.
 */

public class LoginPresenter {
    public static final String TAG = "LoginPresenter";
    private ILoginView view;
    private CloudInteractor cloudInteractor;

    @Inject LoginPresenter(CloudInteractor cloudInteractor) {
        this.cloudInteractor = cloudInteractor;
    }

    public void setView(ILoginView view) {
        this.view = view;
    }

    public void doLogin() {
        cloudInteractor.doLogin(view.getEmail(), view.getPassword(), objects -> {
            if (objects.length > 0) {
                if (objects[0] instanceof Exception) {
                    view.showToast("Login Failed! Please check your credentials," +
                        " or try again later");
                    ((Exception) objects[0]).printStackTrace();
                }
            } else {
                Log.d(TAG, "doLogin: happy flow");
                view.showToast("Success!");
                cloudInteractor.getUserData(objects1 -> {
                    if (objects1[0] instanceof User) {
                        cloudInteractor.updateNewTransactions();
                        view.gotoMainActivity();
                    }
                });
            }
        });
    }

    public void doLoginViaGoogle(GoogleSignInAccount googleSignInAccount) {
        cloudInteractor.doLoginViaGoogle(googleSignInAccount, objects -> {
            if (objects.length > 0) {
                if (objects[0] instanceof Exception) {
                    view.showToast("Failed to get authorization from Google. " +
                        "Please try again or use a different sign in method.");
                    ((Exception) objects[0]).printStackTrace();
                } else {
                    cloudInteractor.setUserData(((Bitmap) objects[0]), ((String) objects[1]),
                        result -> {
                            if (((boolean) result[0])) {
                                Log.d(TAG, "doLoginViaGoogle: success");
                                cloudInteractor.getUserData(objects1 -> {
                                    if (objects1[0] instanceof User) {
                                        cloudInteractor.updateNewTransactions();
                                        view.gotoMainActivity();
                                    }
                                });
                            } else {
                                Log.d(TAG, "doLoginViaGoogle: failure");
                                view.showToast((String) result[1]);
                            }
                        });
                }
            }
        });
    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.Auth.GOOGLE_AUTH_ID)
            .requestEmail()
            .build();
    }
}
