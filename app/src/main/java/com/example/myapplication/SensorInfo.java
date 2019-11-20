package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class SensorInfo {
    private String name;
    private double latitude;
    private double longitude;
    private double value;
    private Date dayAdded;
    private String key;
    private double distance;

    SensorInfo() {

    }

    SensorInfo(String name, double lat, double lon, Date dayAdded, String id) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.dayAdded = dayAdded;
        this.key = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getDayAdded() {
        return dayAdded;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public Object getValueByKey(String key) {
        if (key.equals("Value")) return this.value;
        else if (key.equals("Date Add")) return this.dayAdded;
        else return null;
    }

    public double getDistance() {
        return distance;
    }

    public boolean valueWithinRange(double minValue,double maxValue){
        if(this.value>=minValue&&this.value<=maxValue) return true;
        else return false;
    }
    public boolean dayWithinRange(Date minDate,Date maxDate){
        Date maxDateDefault = Calendar.getInstance().getTime();
        Date minDateDefault = new Date();
        Calendar.getInstance().set(1997,1,1);
        minDateDefault = Calendar.getInstance().getTime();
        if(minDate==null) minDate = minDateDefault;
        if(maxDate==null) maxDate = maxDateDefault;
        Log.d("dayWithinRange","From "+minDate.toString()+" to "+maxDate.toString());
        Log.d("Day after ", String.valueOf(this.dayAdded.after(minDate)));
        Log.d("Day before ", String.valueOf(this.dayAdded.before(maxDate)));
        if(this.dayAdded.after(minDate)&&this.dayAdded.before(maxDate)) {
            Log.d("dayWithinRangeResult", String.valueOf(true));
            return true;
        }
        else return false;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        Log.d("Comparision 2 sensors\n", obj.toString() + "\n" + this.toString());
        if (obj == this) {
            Log.d("Comparision case : ", "1");
            return true;
        }
        if (!(obj instanceof SensorInfo)) {
            Log.d("Comparision case : ", "2");
            return false;
        }
        SensorInfo sensor = (SensorInfo) obj;
        if (sensor.getLatitude() == this.getLatitude() &&
                sensor.getLongitude() == this.getLongitude() && sensor.getName().equals(this.getName())) {
            Log.d("Comparision case : ", "3");
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        String returnInfo = new String();
        returnInfo = "Name : " + this.getName() + "\n" + "Longitude : " + this.getLongitude() + "\nLatitude : " + this.getLatitude() + "\nValue : " + this.getValue() + "\nDay added : " + this.getDayAdded() + "\n";
        return returnInfo;
    }
}
