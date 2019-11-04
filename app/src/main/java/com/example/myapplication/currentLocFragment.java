package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class currentLocFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location location;
    private double lantitude;
    private double longitude;
    private Context mContext;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private SupportMapFragment mapFragment;
    @SuppressLint("ValidFragment")
    public currentLocFragment(Context context){
        this.mContext = context;
    }
    public currentLocFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle("Current Location Menu");

        return inflater.inflate(R.layout.current_location,container,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.currentLocMap);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            startLocationUpdate();
        } else {
            lantitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        setMap(location);
        Log.d("Got Location : ", String.valueOf(lantitude) + " : " + String.valueOf(longitude));
    }
    private void setMap(Location loc){
        final double lan = loc.getLatitude();
        final double lon = loc.getLongitude();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.clear();
                map.addMarker(new MarkerOptions().position(new LatLng(lan,lon)).title("Position : "+lan+" : "+lon));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lan,lon),5));
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("currentLoc","location changed");
    }
}