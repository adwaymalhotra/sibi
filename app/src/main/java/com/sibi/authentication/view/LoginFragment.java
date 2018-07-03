package com.sibi.authentication.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sibi.R;
import com.sibi.authentication._interface.IAuthenticationView;
import com.sibi.authentication._interface.ILoginView;
import com.sibi.authentication.presenter.LoginPresenter;
import com.sibi.util.LogActivity;
import com.sibi.util.dagger.AppComponentProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nandana Yadla on 11/27/2017.
 */

public class LoginFragment extends Fragment implements ILoginView {
    public static final String TAG = "LoginFragment";
    @BindView(R.id.email_et) EditText emailET;
    @BindView(R.id.password_et) EditText passwordET;

    @Inject LoginPresenter presenter;
    private IAuthenticationView parentView;

    @Override public String getEmail() {
        return emailET.getText().toString();
    }

    @Override public String getPassword() {
        return passwordET.getText().toString();
    }

    @Override public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override public void gotoLogActivity() {
        Intent intent = new Intent(getContext(), LogActivity.class);
        startActivity(intent);
    }

    @Override public void gotoMainActivity() {
        parentView.gotoMainActivity();
    }

    @OnClick(R.id.login_btn) public void onLoginClick() {
        presenter.doLogin();
    }

    @OnClick(R.id.register_tv) public void onRegisterClick() {
        parentView.loadRegistrationFragment();
    }

    @OnClick(R.id.login_google_btn) public void doGoogleLogin() {
        this.startActivityForResult(GoogleSignIn.getClient(getContext(),
            presenter.getGoogleSignInOptions()).getSignInIntent(), 1);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                Log.d(TAG, "onActivityResult: Google Success!");
//                Toast.makeText(getContext(), "Authenticated by Google! Now Firebase",
//                        Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = null;
                try {
                    account = task.getResult(ApiException.class);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                presenter.doLoginViaGoogle(account);
            } else {
                Log.d(TAG, "onActivityResult: " + task.getException().getMessage());
//                Log.d(TAG, "onActivityResult: " + GoogleSignInStatusCodes
//                        .getStatusCodeString(Integer.parseInt(task.getException().getMessage())));
                Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        parentView = (IAuthenticationView) context;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        AppComponentProvider.getAppComponent().inject(this);
        presenter.setView(this);
        return view;
    }
}
