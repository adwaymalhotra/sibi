package com.sibi;

import android.app.Application;
import android.util.Log;

import com.sibi.util.PersistenceInteractor;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.util.dagger.AppModule;
import com.sibi.util.dagger.DaggerAppComponent;
import com.sibi.util.firebase.CloudInteractor;

import javax.inject.Inject;

/**
 * Created by adway on 28/11/17.
 */

public class Sibi extends Application {
    public static final String TAG = "Sibi";
    @Inject CloudInteractor cloudInteractor;
    @Inject PersistenceInteractor persistenceInteractor;
    private AppModule appModule;
    private UserUpdateListener userUpdateListener;
//
//    public void updateUser(ICallback callback) {
//        Log.d(TAG, "updateUser: ");
//        cloudInteractor.getUserData(objects -> {
//            if (objects[0] instanceof User) {
//                appModule.setUser((User) objects[0]);
//                callback.onComplete(objects);
//                if (userUpdateListener != null) userUpdateListener.onUserUpdated();
//            }
//        });
//    }

    @Override public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        appModule = new AppModule(getApplicationContext());
        AppComponentProvider.init(DaggerAppComponent.builder()
            .appModule(appModule)
            .build());
        AppComponentProvider.getAppComponent().inject(this);
    }

    public void updateTransactions() {
        if (persistenceInteractor.getTransactions().size() > 0)
            cloudInteractor.updateNewTransactions(result -> {
                if (result[0] instanceof String)
                    Log.d(TAG, "updateUser: " + result[0]);
            });
        else cloudInteractor.updateAllTransactions(result -> {
            if (result[0] instanceof String)
                Log.d(TAG, "updateAllTransactions: " + result[0]);
        });
    }

    public void setUserUpdateListener(UserUpdateListener userUpdateListener) {
        this.userUpdateListener = userUpdateListener;
    }

    public interface UserUpdateListener {
        void onUserUpdated();
    }
}
