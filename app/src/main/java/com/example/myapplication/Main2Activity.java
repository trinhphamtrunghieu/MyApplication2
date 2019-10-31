package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private GoogleMap map;
    private boolean checkPermission = false;
    protected Bundle savedInstanceState;
    private LocationServices locationServices;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationManager locationManager;
    private double lantitude;
    private double longitude;
    private Fragment mVisible;
    private SupportMapFragment mapFragment;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main2);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            setUp();
        }
    }

    private void setUp() {
        Log.d("Calling : ", "On Start");
        setContentView(R.layout.activity_main2);
        final Intent intent = new Intent(this, Main2Activity.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void currentLocFunction() {
       /* Log.d("Current Location : ", String.valueOf(lantitude) + " : " + String.valueOf(longitude));
        Log.d("Calling : ", "On Start");
        final MapView mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.addMarker(new MarkerOptions().position(new LatLng(lantitude, longitude)).title("You are here"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lantitude, longitude), 15));

            }
        });*/

    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void homepageFunction() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SensorInfo sensor;
                sensor = dataSnapshot.getValue(SensorInfo.class);
                Log.d("homepage Function", String.valueOf(sensor.getLatitude()) + " : " + String.valueOf(sensor.getLongitude()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("homepage function", "Failed : " + databaseError.toException());
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.currentLoc) {
            // Handle the camera action
            Log.d("onNavigationItemSelect","Current loc");
            fragment = new currentLocFragment();
        } else if (id == R.id.search) {

        } else if (id == R.id.ranking) {

        } else if (id == R.id.login) {
            startActivity(new Intent(Main2Activity.this, MainActivity.class));
        } else if (id == R.id.home) {
            homepageFunction();

        }
        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean requestPermission(Activity activity, String permission) {
        int request = 0;
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, request);
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        while (!checkPermission) {
            //checkPermission = requestPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            checkPermission = requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            Log.d("Notify", String.valueOf(checkPermission));
        }

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            startLocationUpdate();
            startLocationUpdate();
        } else {
            lantitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        Log.d("Got Location : ", String.valueOf(lantitude) + " : " + String.valueOf(longitude));
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
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

}

