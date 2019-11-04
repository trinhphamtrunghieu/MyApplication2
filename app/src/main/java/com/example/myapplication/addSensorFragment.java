package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class addSensorFragment extends Fragment {
    private Context mContext;
    public addSensorFragment(){};
    @SuppressLint("ValidFragment")
    public addSensorFragment(Context context){
        this.mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getActivity().setTitle("Add sensor menu");
        return inflater.inflate(R.layout.add_sensor,container,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}
