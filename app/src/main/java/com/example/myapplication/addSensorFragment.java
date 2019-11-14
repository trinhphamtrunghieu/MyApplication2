package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addSensorFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context mContext;
    private GoogleApiClient googleApiClient;
    private Location location;
    private double lantitude;
    private double longitude;
    private LocationManager locationManager;
    private LocationRequest locationRequest;

    public addSensorFragment() {
    }

    ;
    private TextView longitudeLabel;
    private TextView latitudeLabel;
    private EditText nameLabel;

    @SuppressLint("ValidFragment")
    public addSensorFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Add sensor menu");
        return inflater.inflate(R.layout.add_sensor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        final Button showButton = view.findViewById(R.id.showInMap);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.map);
                dialog.show();
                MapsInitializer.initialize(getActivity());
                MapView mMapView = (MapView) dialog.findViewById(R.id.map);
                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(lantitude, longitude)).title("You are here"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lantitude, longitude), 15));
                    }
                });
            }
        });
        final Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDatabase();
            }
        });
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        nameLabel = view.findViewById(R.id.nameLabel);
        longitudeLabel = view.findViewById(R.id.longLabel);
        latitudeLabel = view.findViewById(R.id.latLabel);

    }

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
        longitudeLabel.setText(String.valueOf(longitude));
        latitudeLabel.setText(String.valueOf(lantitude));
    }

    private void startLocationUpdate() {
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000).setFastestInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    private void addToDatabase() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference col = db.collection("sensor");
        Query query = col.whereEqualTo("name",String.valueOf(nameLabel.getText()));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String id = db.collection("sensor").document().getId();
                        SensorInfo sensorInfo = new SensorInfo(String.valueOf(nameLabel.getText()), lantitude, longitude, date.toString(), id);
                        db.collection("sensor").document(id).set(sensorInfo);
                    }
                    else{
                        Toast.makeText(mContext, "The sensor with this name has been added already. Select a new name", Toast.LENGTH_LONG).show();
                        nameLabel.setText("");
                    }
                }
            }
        });
    }

}
