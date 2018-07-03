package com.sibi.authentication._interface;

/**
 * Created by adway on 28/11/17.
 */

public interface IRegistrationView {
    String getEmail();
    String getPassword();
    String getName();

    void showToast(String message);
    void redirectToLogin();
}
