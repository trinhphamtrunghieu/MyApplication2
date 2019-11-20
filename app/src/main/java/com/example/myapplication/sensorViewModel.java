package com.example.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class sensorViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<SensorInfo>> sensors = new MutableLiveData<>();
    public MutableLiveData<ArrayList<SensorInfo>> getSensors(){
        return sensors;
    }
    public void setSensors(ArrayList<SensorInfo> sensors){
        this.sensors.setValue(sensors);
    }
}
