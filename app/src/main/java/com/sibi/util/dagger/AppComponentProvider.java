package com.sibi.util.dagger;

/**
 * Created by adway on 28/11/17.
 */

public class AppComponentProvider {
    private static AppComponent appComponent;

    public static void init(AppComponent ac) {
        appComponent = ac;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
