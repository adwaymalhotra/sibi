package com.sibi.primary.drawer._interface;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * Created by adway on 04/12/17.
 */

public interface INavDrawerView {
    int getColorAccent();
    void gotoSplash();
    GoogleSignInClient getGoogleSignInClient();
    void updateDrawer();
    void updateCategoryList();
}
