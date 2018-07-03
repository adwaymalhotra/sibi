package com.sibi.authentication._interface;

/**
 * Created by adway on 28/11/17.
 */

public interface ILoginView {

    String getEmail();
    String getPassword();

    void showToast(String message);
    void gotoLogActivity();
    void gotoMainActivity();
}
