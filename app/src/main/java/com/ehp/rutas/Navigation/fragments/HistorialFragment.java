package com.ehp.rutas.Navigation.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehp.rutas.R;
import com.ehp.rutas.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistorialFragment extends Fragment {

    private String TAG = "HistorialFragment";
    private String USERS = "Users";
    private String ROUTES = "Routes";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private SharedPreferences preferences;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    public HistorialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        preferences = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        getRoutes(preferences.getString("id", ""));


        recycler = (RecyclerView) view.findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(lManager);

        return view;
    }

    public void getRoutes(String idUser) {

        final ArrayList<Route> routes = new ArrayList<>();

        mDatabase.child(USERS).child(idUser).child(ROUTES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (routes.size() > 0)
                    routes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.w(TAG, postSnapshot.getValue().toString());
                    Route route = postSnapshot.getValue(Route.class);
                    routes.add(route);
                }
                Log.w(TAG, "" + routes.size());
                setAdapter(routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAdapter(ArrayList<Route> routes){
        adapter = new RoutesAdapter(routes, getActivity());
        recycler.setAdapter(adapter);
    }
}
