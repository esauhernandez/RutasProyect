package com.ehp.rutas;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RutasApplication extends Application {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String TAG = "RutasApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseCrash.log("Inicializando variables RutasApplication");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    FirebaseCrash.logcat(Log.WARN, TAG, "Usuario logeado " + firebaseUser.getEmail());
                }else {
                    FirebaseCrash.logcat(Log.WARN, TAG, "Usuario No logeado ");
                }
            }
        };

    }
}
