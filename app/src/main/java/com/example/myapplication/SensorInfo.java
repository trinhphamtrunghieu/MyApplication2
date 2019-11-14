package com.example.myapplication;

public class SensorInfo {
    private String name;
    private double latitude;
    private double longitude;
    private double value;
    private String dayAdded;
    private String key;
    SensorInfo(String name,double lat,double lon,String dayAdded,String id){
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.dayAdded = dayAdded;
        this.key = id;
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
