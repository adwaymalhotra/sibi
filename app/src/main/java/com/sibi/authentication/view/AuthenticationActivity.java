package com.sibi.authentication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.sibi.R;
import com.sibi.authentication._interface.IAuthenticationView;
import com.sibi.primary.MainActivity;
import com.sibi.util.Utils;

/**
 * Created by adway on 28/11/17.
 */

public class AuthenticationActivity extends AppCompatActivity implements IAuthenticationView {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loadLoginFragment();
    }

    @Override public void loadLoginFragment() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        if (f == null) f = new LoginFragment();
        f.setSharedElementEnterTransition(new Utils.FragmentTransition());
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, f, LoginFragment.TAG)
            .commit();
    }

    @Override public void loadRegistrationFragment() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new RegistrationFragment())
            .addToBackStack(RegistrationFragment.TAG)
            .commit();
    }

    @Override public void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else super.onBackPressed();
    }
}
