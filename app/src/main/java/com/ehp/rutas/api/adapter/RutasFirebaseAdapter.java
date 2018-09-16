package com.ehp.rutas.api.adapter;

import android.content.Context;
import android.util.Log;

import com.ehp.rutas.api.services.RutasFirebaseService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RutasFirebaseAdapter {

    private static final String FIREBASE_DATABASE_URL = "https://rutas-552f6.firebaseio.com/";

    public RutasFirebaseService getFirebaseService(final Context context) {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + getToken(context))
                        .build()
                        ;
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FIREBASE_DATABASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                ;

        return retrofit.create(RutasFirebaseService.class);
    }

    private String getToken(Context context) throws IOException {

        GoogleCredential googleCredential
                = GoogleCredential.fromStream(context.getAssets().open("rutas-552f6-firebase-adminsdk-mvord-c6f60ffa56.json"));
        GoogleCredential scoped = googleCredential.createScoped(
                Arrays.asList(
                        "https://www.googleapis.com/auth/firebase.database",
                        "https://www.googleapis.com/auth/userinfo.email"
                )
        );

        scoped.refreshToken();
        Log.w("RETROFIT", "TOKEN: " + scoped.getAccessToken());
        return scoped.getAccessToken();
    }


}
