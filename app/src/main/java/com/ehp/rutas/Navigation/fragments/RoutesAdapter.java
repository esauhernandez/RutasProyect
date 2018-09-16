package com.ehp.rutas.Navigation.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ehp.rutas.R;

import java.util.ArrayList;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> implements ItemClickListener {

    private ArrayList<Route> routes;
    private final Context context;

    public RoutesAdapter(ArrayList<Route> routes, Context context) {
        this.routes = routes;
        this.context = context;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(context, "Item seleccionado: " + position, Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idRoute", routes.get(position).getId());
        editor.commit();

        Intent intent = new Intent(context, DetailRouteActivity.class);
        context.startActivity(intent);
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView origen;
        public TextView destino;
        public TextView totalTime;
        public TextView totalDistance;
        public ItemClickListener listener;

        public RouteViewHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            origen = (TextView) itemView.findViewById(R.id.cardRoute_origen);
            destino = (TextView) itemView.findViewById(R.id.cardRoute_destino);
            totalTime = (TextView) itemView.findViewById(R.id.cardRoute_titmepo);
            totalDistance = (TextView) itemView.findViewById(R.id.cardRoute_distance);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_route, viewGroup, false);

        return new RouteViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int i) {
        routeViewHolder.origen.setText("Origen: " + routes.get(i).getOrigen());
        routeViewHolder.destino.setText("Destino: " + routes.get(i).getDestino());
        routeViewHolder.totalTime.setText("Tiempo: " + routes.get(i).getTotalTime());
        routeViewHolder.totalDistance.setText("Distancia: " + routes.get(i).getTotalDistance());
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }
}

interface ItemClickListener {
    void onItemClick(View view, int position);
}
