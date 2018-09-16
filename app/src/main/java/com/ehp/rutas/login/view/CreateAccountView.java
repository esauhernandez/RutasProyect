package com.ehp.rutas.login.view;

public interface CreateAccountView {

    void showProgressBar();
    void hideProgressBar();

    void createAccountError(String error);

    void goHome();
}
