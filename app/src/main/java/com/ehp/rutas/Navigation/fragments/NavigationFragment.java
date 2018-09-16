package com.ehp.rutas.Navigation.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ehp.rutas.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "NavigationFragment";
    private SupportMapFragment supportMapFragment;
    private GoogleMap mGoogleMap;
    private View mView;
    private String direccion;
    private double lat;
    private double lng;
    private Marker marker;
    private static int ACCES_FINE_LOCATION = 101;
    private static int TIME_UPDATE_LOCATION = 1000;
    private static int DISTANCE_UPDATE_LOCATION = 0;

    private ArrayList<LatLng> originDestination = new ArrayList<>();
    private SharedPreferences sharedPref;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private SharedPreferences preferences;
    FragmentManager manager;

    public static NavigationFragment newInstance() {
        NavigationFragment fragment = new NavigationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        PlaceAutocompleteFragment placeAutocompleteFragmentOrigin = null;
        PlaceAutocompleteFragment placeAutocompleteFragmentDestination = null;

        placeAutocompleteFragmentOrigin = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origin);
        placeAutocompleteFragmentDestination = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destination);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        preferences = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
        supportMapFragment.getMapAsync(this);

        placeAutocompleteFragmentOrigin.setFilter(new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build());
        placeAutocompleteFragmentDestination.setFilter(new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build());

        placeAutocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                originDestination.clear();
                mGoogleMap.clear();
                final LatLng latLng = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.position(latLng);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.animateCamera(cameraUpdate);
                originDestination.add(latLng);
            }

            @Override
            public void onError(Status status) {

            }
        });

        placeAutocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                final LatLng latLng = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.position(latLng);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.animateCamera(cameraUpdate);
                originDestination.add(latLng);
                if (originDestination.size() == 2) {
                    String url = getRequestUrl(originDestination.get(0), originDestination.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections(mGoogleMap, getContext());
                    taskRequestDirections.execute(url);
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mMapView = (MapView) mView.findViewById(R.id.map);

        /*if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }*/

        sharedPref = getContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(getContext());
        myLocation();
        final String[] reqUrl = {""};

        SharedPreferences.Editor editor = preferences.edit();

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (originDestination.size() == 2) {
                    originDestination.clear();
                    mGoogleMap.clear();
                }
                originDestination.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (originDestination.size() == 1) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mGoogleMap.addMarker(markerOptions);

                if (originDestination.size() == 2) {
                    String url = getRequestUrl(originDestination.get(0), originDestination.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections(mGoogleMap, getContext());
                    taskRequestDirections.execute(url);
                    reqUrl[0] = url;
                }
            }
        });

        editor.putString("reqUrl", reqUrl[0]);
        editor.commit();
        /*LatLng latLngInitial = new LatLng(19, -99);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(latLngInitial).title("Título").snippet("Posición designada"));
        CameraPosition cameraPosition = CameraPosition.builder().target(latLngInitial).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
    }

    private void setLocation(Location location){
        if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
            try{
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address>  listAddress = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                if(listAddress.isEmpty()){
                    Address address = listAddress.get(0);
                    direccion = address.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void locationStart(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled){
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }

    private void updateLocation(Location location){
        if(location != null){
            lat = location.getLatitude();
            lng = location.getLongitude();
            setMarker(lat, lng);
        }
    }

    private void setMarker(double lat, double lng){
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        if(marker != null)marker.remove();
        marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Dirección: " + direccion));
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void myLocation(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            return;
        }else{
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, TIME_UPDATE_LOCATION, DISTANCE_UPDATE_LOCATION, locationListener);
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = ConstantsPetition.ORIGIN + "=" + origin.latitude +","+origin.longitude;
        String str_dest = ConstantsPetition.DESTINATION + "=" + dest.latitude + "," + dest.longitude;
        String sensor = ConstantsPetition.SENSOR_FALSE;
        String mode = ConstantsPetition.MODE_DRIVING;
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        String output = ConstantsPetition.FORMAT_OUTPUT_JSON;
        String url = ConstantsPetition.ROOT_URL + output + "?" + param;

        return url;
    }

    public void starRoute(View view){
        Route route = new Route();
        route = getCurrentRoute();
        //saveRoute(route);
    }

    public void saveRoute(Route route) {
        /*RutasFirebaseAdapter rutasFirebaseAdapter = new RutasFirebaseAdapter();
        RutasFirebaseService rutasFirebaseService = rutasFirebaseAdapter.getFirebaseService(getContext());
        Call<JsonObject> call = rutasFirebaseService.createRoute(route);*/

        mDatabase.child("Users").child(sharedPref.getString("id", "")).setValue(route);

    /*call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getContext(), "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Ocurrrió el erroor: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });*/
    }

    public Route getCurrentRoute(){
        Route route = new Route();
        return route;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //updateLocation(location);
            //setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
            locationStart();
        }
    };

}
