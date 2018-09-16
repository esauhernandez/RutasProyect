package com.ehp.rutas.Navigation.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ehp.rutas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailRouteActivity extends AppCompatActivity {

    private static final String TAG = "DetailRouteActivity";
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;


    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_route);

        ArrayList<Indications> indications = new ArrayList<>();
        preferences = getSharedPreferences("USER", Context.MODE_PRIVATE);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.collapser);
        collapser.setTitle("Indicaciones");

        recycler = (RecyclerView) findViewById(R.id.recyclerDetailRoute);
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        getIndications();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    public void getIndications() {

        final ArrayList<Indications> indications = new ArrayList<>();

        Log.w("Datos:", preferences.getString("idRoute", ""));

        mDatabase.child("Users").child(preferences.getString("id", "")).child("Routes").child(preferences.getString("idRoute", "")).child("indcations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (indications.size() > 0)
                    indications.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.w("Datos:", postSnapshot.getValue().toString());
                    Indications indication = postSnapshot.getValue(Indications.class);
                    indications.add(indication);
                }
                Log.w(TAG, "" + indications.size());
                setAdapter(indications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAdapter(ArrayList<Indications> indications){
        adapter = new IndicationsAdapter(indications);
        recycler.setAdapter(adapter);
    }

}
