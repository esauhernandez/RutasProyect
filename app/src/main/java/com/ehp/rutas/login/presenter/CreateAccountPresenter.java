package com.ehp.rutas.login.presenter;

import android.content.Context;

import com.ehp.rutas.model.User;

public interface CreateAccountPresenter {

    void createAccount(User user, Context context); // Interactor
    void createAccountSuccess();
    void createAccountError(String error);
}
