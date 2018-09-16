package com.ehp.rutas.Navigation.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehp.rutas.R;

import java.util.ArrayList;

public class IndicationsAdapter extends RecyclerView.Adapter<IndicationsAdapter.IndicationsViewHolder> {
    private ArrayList<Indications> indications;

    public IndicationsAdapter(ArrayList<Indications> indications) {
        this.indications = indications;
    }

    public class IndicationsViewHolder extends RecyclerView.ViewHolder {
        public TextView indicationDescription;
        public TextView distance;
        public TextView tiempo;
        public ImageView imgDirection;

        public IndicationsViewHolder(@NonNull View itemView) {
            super(itemView);
            indicationDescription = (TextView)itemView.findViewById(R.id.cardIndication_indication);
            distance = (TextView)itemView.findViewById(R.id.cardIndication_distance);
            tiempo = (TextView)itemView.findViewById(R.id.cardIndication_time);
            imgDirection = (ImageView)itemView.findViewById(R.id.cardIndicator_imgDirection);
        }
    }

    @NonNull
    @Override
    public IndicationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_indication, viewGroup, false);
        return new IndicationsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicationsViewHolder indicationsViewHolder, int i) {
        indicationsViewHolder.indicationDescription.setText(indications.get(i).getHtmlInstruction());
        indicationsViewHolder.distance.setText(indications.get(i).getDistance());
        indicationsViewHolder.tiempo.setText(indications.get(i).getDuration());
    }

    @Override
    public int getItemCount() {
        return indications.size();
    }

}
