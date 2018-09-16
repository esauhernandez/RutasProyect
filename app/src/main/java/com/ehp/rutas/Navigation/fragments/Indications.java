package com.ehp.rutas.Navigation.fragments;

public class Indications {

    private String distance;
    private String duration;
    private String htmlInstruction;
    private String maneuver;

    public Indications(String distance, String duration, String htmlInstruction, String maneuver) {
        this.distance = distance;
        this.duration = duration;
        this.htmlInstruction = htmlInstruction;
        this.maneuver = maneuver;
    }

    public Indications() {
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
