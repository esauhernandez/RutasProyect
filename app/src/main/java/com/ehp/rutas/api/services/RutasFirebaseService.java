package com.ehp.rutas.api.services;

import com.ehp.rutas.Navigation.fragments.Route;
import com.ehp.rutas.model.User;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RutasFirebaseService {

    @POST("Users.json")
    Call<JsonObject> createUser(@Body User user);

    @POST("Routes.json")
    Call<JsonObject> createRoute(@Body Route route);

}
