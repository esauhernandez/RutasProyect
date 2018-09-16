package com.ehp.rutas.Navigation.fragments;

import java.util.ArrayList;

public class Route {

    private String id;
    private String imageUrl;
    private String origen;
    private String destino;
    private String totalTime;
    private String totalDistance;
    private ArrayList<Indications> Indcations;

    public Route(String id, String imageUrl, String origen, String destino, String totalTime, String totalDistance, ArrayList<Indications> indcations) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.origen = origen;
        this.destino = destino;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        Indcations = indcations;
    }

    public Route() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public ArrayList<Indications> getIndcations() {
        return Indcations;
    }

    public void setIndcations(ArrayList<Indications> indcations) {
        Indcations = indcations;
    }
}
