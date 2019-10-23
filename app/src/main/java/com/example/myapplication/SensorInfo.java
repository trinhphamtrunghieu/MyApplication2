package com.example.myapplication;

import java.util.Date;

public class SensorInfo {
    private String name;
    private double latitude;
    private double longitude;
    private double value;
    private String dayAdded;
    private String key;
    SensorInfo(String name,double lat,double lon,String dayAdded){
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.dayAdded = dayAdded;
    }
    public double getLatitude() {
        return latitude;
    }
    public String getName(){
        return name;
    }
    public double value(){
        return value;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getDayAdded(){return dayAdded;}
    public void setKey(String key){
        this.key = key;
    }
    public String getKey(){ return key;}
}
