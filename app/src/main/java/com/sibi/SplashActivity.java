package com.sibi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sibi.authentication.view.AuthenticationActivity;
import com.sibi.model.User;
import com.sibi.primary.MainActivity;
import com.sibi.util.Alarms;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.dagger.AppComponentProvider;
import com.sibi.util.firebase.CloudInteractor;

import javax.inject.Inject;

/**
 * Created by adway on 22/11/17.
 */

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";
    @Inject CloudInteractor cloudInteractor;
    @Inject PersistenceInteractor persistenceInteractor;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppComponentProvider.getAppComponent().inject(this);
        Alarms.createAlarmForNotification(getApplicationContext(),
            System.currentTimeMillis()+61000);
        Intent intent;
        if (cloudInteractor.isLoggedIn()) {
//            persistenceInteractor.storeEmail(cloudInteractor.getCurrentUser().getEmail());
            intent = new Intent(SplashActivity.this, MainActivity.class);
            cloudInteractor.updateNewTransactions(results -> Log.d(TAG, "onCreate: " + results[0]));
            cloudInteractor.getUserData(objects -> {
                if (objects[0] instanceof User) {
                    startActivity(intent);
                    finish();
                }
            });
        } else {
//            persistenceInteractor.storeEmail("");
            intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, findViewById(R.id.image),
                    "image");
            new Handler().postDelayed(() -> {
                startActivity(intent, options.toBundle());
                finish();
            }, 1000);
        }
    }
}
