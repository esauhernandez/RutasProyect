package com.ehp.rutas.login.repository;

import android.content.Context;

import com.ehp.rutas.model.User;

public interface CreateAccountRepository {
    void createAccount(User user, Context context); // Interactor
}
