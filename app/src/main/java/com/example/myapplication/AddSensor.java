package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddSensor extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
,GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener{
    private double longitude;
    private double lantitude;
    private LocationManager locationManager;
    private Location location;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private GoogleMap gMap;
    private int id;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    public void showPopup(View v){
        Log.d("Notify showPopup","activated ");
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map);
        dialog.show();
        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);
        mMapView = (MapView) dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
                public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.addMarker(new MarkerOptions().position(new LatLng(lantitude,longitude)));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lantitude,longitude),15));
            }
        });
    }
    @SuppressLint("MissingPermission")
    protected void startLocationUpdate(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }
    protected void addToDatabase(View view){
        String keyID = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        TextView nameField = findViewById(R.id.nameLabel);
        Button showButton = findViewById(R.id.showInMap);
        Button addButton = findViewById(R.id.addButton);
        SensorInfo sensor = new SensorInfo(String.valueOf(nameField.getText()),lantitude,longitude, DateFormat.getDateTimeInstance().format(new Date()));
        Log.d("ID", String.valueOf(id));
        sensor.setKey(keyID);
        myRef.child("sensor").child(keyID).setValue(sensor);
        Toast.makeText(AddSensor.this,"Add successfully",Toast.LENGTH_SHORT).show();
        nameField.setEnabled(false);
        showButton.setEnabled(false);
        addButton.setText("Back to previous");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddSensor.this,MainActivity.class));
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }
        });
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location==null){
            startLocationUpdate();
        }
        else{
            lantitude = location.getLatitude();
            longitude = location.getLongitude();
            TextView longitudeLabel = findViewById(R.id.longLabel);
            TextView latitudeLabel = findViewById(R.id.latLabel);
            longitudeLabel.setText(String.valueOf(longitude));
            latitudeLabel.setText(String.valueOf(lantitude));
        }
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
    }
    @Override
    public void onStart(){
        super.onStart();
        googleApiClient.connect();
    }
}
