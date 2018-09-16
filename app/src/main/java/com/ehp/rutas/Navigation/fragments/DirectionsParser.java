package com.ehp.rutas.Navigation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DebugUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DirectionsParser {

    Context context;

    public DirectionsParser(Context context) {
        this.context = context;
    }

    private static final String TAG = "DirectionsParser";

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePolyline(polyline);

                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lon", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Almacena en base de datos remota
        Route routeObj = parseToObj(jObject);
        saveRouteDataAtDatabase(routeObj);

        Log.w(TAG, "Points: " + routes);

        return routes;
    }

    public Route parseToObj(JSONObject jObject) {

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        Route route = new Route();
        ArrayList<Indications> indications = new ArrayList<>();

        route.setId(UUID.randomUUID().toString());
        route.setImageUrl("");

        try {

            jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    route.setOrigen((String) (((JSONObject) jLegs.get(j)).get("start_address")));
                    route.setDestino((String) (((JSONObject) jLegs.get(j)).get("end_address")));
                    route.setTotalTime((String) ((JSONObject)((JSONObject) jLegs.get(j)).get("duration")).get("text"));
                    route.setTotalDistance((String) ((JSONObject)((JSONObject) jLegs.get(j)).get("distance")).get("text"));

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        Indications indication = new Indications();
                        String maneuver = "";
                        if(k > 0){
                            maneuver = (String) ((JSONObject) jSteps.get(k)).get("maneuver");
                        }

                        indication.setDistance((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("text"));
                        indication.setDuration((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("duration")).get("text"));
                        indication.setHtmlInstruction((String) ((JSONObject) jSteps.get(k)).get("html_instructions"));
                        if(maneuver != null || !maneuver.isEmpty()){
                            indication.setManeuver(maneuver);
                        }else{
                            indication.setManeuver("");
                        }

                        indications.add(indication);
                    }
                }
                route.setIndcations(indications);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return route;
    }


    private List decodePolyline(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void saveRouteDataAtDatabase(Route route){
        if(route.getOrigen() != null){
            SharedPreferences preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(preferences.getString("id", "")).child("Routes").child(route.getId()).setValue(route);
        }

    }
}