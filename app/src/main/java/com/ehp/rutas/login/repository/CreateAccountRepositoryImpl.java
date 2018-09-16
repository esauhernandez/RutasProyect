package com.ehp.rutas.login.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ehp.rutas.api.adapter.RutasFirebaseAdapter;
import com.ehp.rutas.api.services.RutasFirebaseService;
import com.ehp.rutas.login.presenter.CreateAccountPresenter;
import com.ehp.rutas.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateAccountRepositoryImpl implements CreateAccountRepository {

    private static final String TAG = "CreateAccountRepository";
    private CreateAccountPresenter presenter;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public CreateAccountRepositoryImpl(CreateAccountPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void createAccount(User user, final Context context) {
        RutasFirebaseAdapter rutasFirebaseAdapter = new RutasFirebaseAdapter();
        RutasFirebaseService rutasFirebaseService = rutasFirebaseAdapter.getFirebaseService(context);
        Call<JsonObject> call = rutasFirebaseService.createUser(user);

        mDatabase.child("Users").child(user.getId()).setValue(user);

        SharedPreferences preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", user.getEmail());
        editor.putString("username", user.getUsername());
        editor.putString("id", user.getId());
        editor.commit();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.w(TAG, "RESPONSE ~ " +  response);
                Log.w(TAG, "RESPONSE ~ " +  response.body());
                presenter.createAccountSuccess();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                presenter.createAccountError(t.toString());
                Log.e(TAG, t.toString());
                t.printStackTrace();
            }
        });
    }
}
