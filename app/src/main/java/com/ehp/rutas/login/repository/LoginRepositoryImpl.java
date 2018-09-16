package com.ehp.rutas.login.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ehp.rutas.login.presenter.LoginPresenter;
import com.ehp.rutas.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRepositoryImpl implements LoginRepository {

    LoginPresenter presenter;
    private String TAG = "LoginRepositoryImpl";
    private String USERS = "Users";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private SharedPreferences preferences;

    public LoginRepositoryImpl(LoginPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void signIn(final String username, String password, final Activity activity, FirebaseAuth firebaseAuth) {

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();

                            guardaInfo(user, activity);

                            FirebaseCrash.logcat(Log.WARN, TAG, "Usuario logeado " + user.getEmail());
                            presenter.loginSuccess();
                        } else {
                            FirebaseCrash.logcat(Log.ERROR, TAG, "Ocurrió un Error");
                            presenter.loginError("Ocurrió un Error");
                        }
                    }
                });


    }

    public void guardaInfo(final FirebaseUser firebaseUser, final Activity activity) {

        //mDatabase.child(USERS);
        final ArrayList<User> list_users = new ArrayList<>();
        Log.w("Datos:", firebaseUser.getUid());

            mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(list_users.size() > 0)
                    list_users.clear();
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    for(DataSnapshot uSnapshot:postSnapshot.getChildren()){
                        User user = uSnapshot.getValue(User.class);
                        Log.w("Datos", uSnapshot.getValue().toString());
                        list_users.add(user);
                    }

                }

                Log.w("Datos", "" + list_users.size());

                preferences = activity.getSharedPreferences("USER", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", firebaseUser.getEmail());

                for(int i = 0; i <list_users.size(); i++){
                    if(list_users.get(i).getEmail().equals(firebaseUser.getEmail())){
                        Log.w("Datos", list_users.get(i).getUsername());
                        editor.putString("username", list_users.get(i).getEmail());
                        editor.putString("id", list_users.get(i).getId());
                    }
                }
                editor.commit();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}