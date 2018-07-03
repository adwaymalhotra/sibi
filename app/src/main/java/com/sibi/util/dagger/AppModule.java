package com.sibi.util.dagger;

import android.content.Context;
import android.preference.PreferenceManager;

import com.sibi.model.User;
import com.sibi.util.PersistenceInteractor;
import com.sibi.util.firebase.CloudInteractor;
import com.sibi.util.retrofit.YoutubeAPIProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by adway on 28/11/17.
 */

@Module
public class AppModule {
    public static final String TAG = "AppModule";
    private User user = new User();
    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

//    public void setUser(User user) {
//        Log.d(TAG, "setUser: ");
//        this.user = user;
//    }

//    @Provides @Singleton User provideUserObject() {
//        return user;
//    }

    @Provides @Singleton PersistenceInteractor providesPersistenceInteractor() {
        return new PersistenceInteractor(PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Provides @Singleton
    CloudInteractor providesFirebaseInteractor(PersistenceInteractor persistenceInteractor) {
        return new CloudInteractor(persistenceInteractor, context);
    }

    @Provides @Singleton YoutubeAPIProvider providesYoutubeAPI() {
        return new YoutubeAPIProvider();
    }
}