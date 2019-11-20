package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class sensorAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SensorInfo> sensors;
    private LayoutInflater layoutInflater;
    public sensorAdapter(Context context,ArrayList<SensorInfo> sensors){
        this.mContext = context;
        this.sensors = sensors;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Object getItem(int position) {
        return sensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item,null);
            holder = new ViewHolder();
            holder.nameSensor = convertView.findViewById(R.id.nameSensor);
            holder.dateLabel = convertView.findViewById(R.id.dateLabel);
            holder.latLabel = convertView.findViewById(R.id.latLabel);
            holder.longLabel = convertView.findViewById(R.id.longLabel);
            holder.valueLabel = convertView.findViewById(R.id.valueLabel);
            holder.layout = convertView.findViewById(R.id.itemBackground);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        SensorInfo sensor = this.sensors.get(position);
        double value;
        holder.nameSensor.setText(sensor.getName());
        holder.longLabel.setText(String.valueOf(sensor.getLongitude()));
        holder.latLabel.setText(String.valueOf(sensor.getLatitude()));
        holder.dateLabel.setText(sensor.getDayAdded().toString());
        DecimalFormat df = new DecimalFormat("#.00");
        value = sensor.getValue();
//        value = Double.parseDouble(value);
        holder.valueLabel.setText(String.valueOf(df.format(value)));
        if(value>=0&&value<=50) holder.layout.setBackgroundColor(Color.rgb((int)(2.56*Math.abs(50-value)),255,0)); //xanh la
        else if(value>50&&value<=100) holder.layout.setBackgroundColor((Color.rgb((int)(2.56*Math.abs(100-value)+128),128,0))); // vang
        else if(value>100&&value<=150) holder.layout.setBackgroundColor(Color.rgb(255,255-(int)(2.56*Math.abs(150-value))+1,0)); //cam
        else if(value>150&&value<=200) holder.layout.setBackgroundColor(Color.rgb(255,128-(int)(2.56*Math.abs(200-value)),0)); //do
        else if(value>200&&value<=300) holder.layout.setBackgroundColor(Color.rgb(255,0,(int)(2.56*Math.abs(300-value))));//tim
        else holder.layout.setBackgroundColor(Color.rgb(255-(int)(2.56*Math.abs(100-value)),0,255-(int)(2.56*Math.abs(100-value))));//nau
        return convertView;
    }
    static class ViewHolder{
        TextView nameSensor;
        TextView latLabel;
        TextView longLabel;
        TextView dateLabel;
        TextView valueLabel;
        ConstraintLayout layout;
    }
}
