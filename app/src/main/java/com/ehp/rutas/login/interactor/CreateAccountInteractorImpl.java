package com.ehp.rutas.login.interactor;

import android.content.Context;

import com.ehp.rutas.login.presenter.CreateAccountPresenter;
import com.ehp.rutas.login.repository.CreateAccountRepository;
import com.ehp.rutas.login.repository.CreateAccountRepositoryImpl;
import com.ehp.rutas.model.User;

public class CreateAccountInteractorImpl implements CreateAccountInteractor{

    private CreateAccountPresenter presenter;
    private CreateAccountRepository repository;

    public CreateAccountInteractorImpl(CreateAccountPresenter presenter) {
        this.presenter = presenter;
        repository = new CreateAccountRepositoryImpl(presenter);
    }


    @Override
    public void createAccount(User user, Context context) {
        repository.createAccount(user, context);
    }
}
