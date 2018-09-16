package com.ehp.rutas.login.presenter;

import android.content.Context;

import com.ehp.rutas.login.interactor.CreateAccountInteractor;
import com.ehp.rutas.login.interactor.CreateAccountInteractorImpl;
import com.ehp.rutas.login.view.CreateAccountView;
import com.ehp.rutas.model.User;

public class CreateAccountPresenterImpl implements CreateAccountPresenter {

    private CreateAccountView createAccountView;
    private CreateAccountInteractor interactor;

    public CreateAccountPresenterImpl(CreateAccountView createAccountView) {
        this.createAccountView = createAccountView;
        interactor = new CreateAccountInteractorImpl(this);
    }

    @Override
    public void createAccount(User user, Context context) {
        createAccountView.showProgressBar();
        interactor.createAccount(user, context);
    }

    @Override
    public void createAccountSuccess() {
        createAccountView.goHome();
        createAccountView.hideProgressBar();
    }

    @Override
    public void createAccountError(String error) {
        createAccountView.createAccountError(error);
    }
}
