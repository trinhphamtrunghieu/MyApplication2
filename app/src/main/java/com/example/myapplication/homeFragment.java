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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

public class homeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private Context mContext;
    private GridView gridInfo;
    private RadioButton radioAsc;
    private RadioButton radioDes;
    private Spinner spinner;
    private double latitude;
    private double longitude;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private Location location;
    private Bundle bundle;
    public  static ArrayList<SensorInfo> sensors = new ArrayList<>();
    private sensorViewModel viewModel;
    private static String checkedButton = new String();
    private static String selectedAttr = new String();
    public homeFragment() {
    }

    @SuppressLint("ValidFragment")
    public homeFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(sensorViewModel.class);
    }
    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        Log.d(TAG,"Call from : "+getActivity());
        getActivity().setTitle("Home Menu");
        setRetainInstance(true);
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bundle = savedInstanceState;
        getInfoFromDatabase();
        gridInfo = view.findViewById(R.id.gridInfo);
        spinner = view.findViewById(R.id.spinnerOrder);
        radioAsc = view.findViewById(R.id.radioButton4);
        radioDes = view.findViewById(R.id.radioDes);
        if(this.mContext==null) this.mContext = view.getContext();
        checkedButton = "Asc.";
        addItemToSpinner();
        CompoundButton.OnCheckedChangeListener listenerRadio = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedButton = (String) buttonView.getText();
                    sort(checkedButton,selectedAttr);
                    showToGrid(sensors);
                }
            }
        };
        radioAsc.setOnCheckedChangeListener(listenerRadio);
        radioDes.setOnCheckedChangeListener(listenerRadio);
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

    }

    @SuppressLint("LongLogTag")
    private void getInfoFromDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection("sensor").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("getInfoFromDatabase", "Data changed failed");
                    return;
                }
                String source = queryDocumentSnapshots != null && queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "Local" : "Server";
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, source + " data: ADDED");
                            if((sensors==null)||(!sensors.contains(dc.getDocument().toObject(SensorInfo.class)))) sensors.add(dc.getDocument().toObject(SensorInfo.class));
                            Log.d(TAG,dc.getDocument().toObject(SensorInfo.class).toString());
                            break;
                        case REMOVED:
                            Log.d(TAG, source + " data: REMOVE "+dc.getDocument().toObject(SensorInfo.class).toString());
                            if(sensors.contains(dc.getDocument().toObject(SensorInfo.class))) sensors.remove(dc.getDocument().toObject(SensorInfo.class));
                            break;
                        case MODIFIED:
                            Log.d(TAG, source + " data: MODIFIED");
                            Log.d(TAG,dc.getDocument().toObject(SensorInfo.class).toString());
                            for(SensorInfo sensor : sensors){
                                if(sensor.equals(dc.getDocument().toObject(SensorInfo.class))){
                                    sensor.setValue(dc.getDocument().toObject(SensorInfo.class).getValue());
                                    Log.d(TAG,sensor.toString());
                                }
                            }
                            break;
                    }
                    viewModel.setSensors(sensors);
                }
                if(sensors!=null) {
                    sort("Asc.","Value");
                    showToGrid(sensors);
                }
            }
        });
    }
    private void showToGrid(ArrayList<SensorInfo> sensors){
        viewModel.setSensors(sensors);
        gridInfo.setAdapter(new sensorAdapter(mContext,sensors));
    }
    private void addItemToSpinner(){
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("Value");
        itemList.add("Distance");
        itemList.add("Date add");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAttr = spinner.getSelectedItem().toString();
                sort(checkedButton,selectedAttr);
                showToGrid(sensors);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG,"Trigger dropdown but nothing selected");
            }
        });
    }
    private void sort(String type,String attr){
        int t;
        if (type.equals("Asc.")) t = 1;
        else t = 2;
        if(attr.equals("Value")) quicksortValue(attr,0,sensors.size()-1,t);
        else if(attr.equals("Date add")) quicksortDate(attr,0,sensors.size()-1,t);
        else if(attr.equals("Distance")){
            sortDistanceDriver(t);
        }
    }
    private void quicksortValue(String attr,int low,int high,int type){
        if(low<high){
            int pi = partitionValue(attr,low,high,type);
            quicksortValue(attr,low,pi-1,type);
            quicksortValue(attr,pi+1,high,type);
        }

    }
    private int partitionValue(String attr,int low,int high,int type){
        Double pivot = (Double) sensors.get(high).getValueByKey(attr);
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (((((Double)sensors.get(j).getValueByKey(attr) < pivot)&&type==1))||((Double)sensors.get(j).getValueByKey(attr) > pivot)&&type==2)
            {
                i++;

                // swap arr[i] and arr[j]
                SensorInfo temp = sensors.get(i);
                sensors.set(i,sensors.get(j));
                sensors.set(j,temp);
            }
        }
        // swap arr[i+1] and arr[high] (or pivot)
        SensorInfo temp = sensors.get(i+1);
        sensors.set(i+1,sensors.get(high));
        sensors.set(high,temp);
        return i+1;
    };
    private void quicksortDate(String attr,int low,int high,int type){
        if(low<high){
            int pi = partitionDate(attr,low,high,type);
            quicksortDate(attr,low,pi-1,type);
            quicksortDate(attr,pi+1,high,type);
        }

    }
    private int partitionDate(String attr,int low,int high,int type){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date pivot = sensors.get(high).getDayAdded();
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (((sensors.get(j).getDayAdded().after(pivot)&&type==1))||(sensors.get(j).getDayAdded().before(pivot))&&type==2)
            {
                i++;

                // swap arr[i] and arr[j]
                SensorInfo temp = sensors.get(i);
                sensors.set(i,sensors.get(j));
                sensors.set(j,temp);
            }
        }
        // swap arr[i+1] and arr[high] (or pivot)
        SensorInfo temp = sensors.get(i+1);
        sensors.set(i+1,sensors.get(high));
        sensors.set(high,temp);
        return i+1;
    };
    @SuppressLint("LongLogTag")
    private void sortDistanceDriver(int type){
        Log.d("TAG","Sort by distance triggered "+ location);
        if(location!=null){
            for(SensorInfo sensor : sensors){
                sensor.setDistance(calculateDistance(latitude,longitude,sensor.getLatitude(),sensor.getLongitude()));
                Log.d(TAG, String.valueOf(sensor.getDistance()));
            }
            quicksortDistance("Distance",0,sensors.size()-1,type);
        }
    }
    private double calculateDistance(double lat1, double long1, double lat2, double long2){
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(long2 - long1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
    private void quicksortDistance(String attr,int low,int high,int type){
        if(low<high){
            int pi = partitionDistance(attr,low,high,type);
            quicksortDistance(attr,low,pi-1,type);
            quicksortDistance(attr,pi+1,high,type);
        }

    }
    private int partitionDistance(String attr,int low,int high,int type){
        Double pivot = sensors.get(high).getDistance();
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than the pivot
            if (((sensors.get(j).getDistance()>pivot&&type==1))||(sensors.get(j).getDistance()<pivot)&&type==2)
            {
                i++;

                // swap arr[i] and arr[j]
                SensorInfo temp = sensors.get(i);
                sensors.set(i,sensors.get(j));
                sensors.set(j,temp);
            }
        }
        // swap arr[i+1] and arr[high] (or pivot)
        SensorInfo temp = sensors.get(i+1);
        sensors.set(i+1,sensors.get(high));
        sensors.set(high,temp);
        return i+1;
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location!=null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        else{
            startLocationUpdate();
        }
        Log.d("Location : ", String.valueOf(latitude)+" : "+String.valueOf(longitude));
    }

    private void startLocationUpdate(){
        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000).setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}