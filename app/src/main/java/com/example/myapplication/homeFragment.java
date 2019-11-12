package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class homeFragment extends Fragment {
    private Context mContext;
    private  SensorInfo[] sensor = null;
    public homeFragment(){
    }
    @SuppressLint("ValidFragment")
    public homeFragment(Context context){
        this.mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState){
        getActivity().setTitle("Home Menu");
        return inflater.inflate(R.layout.home,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getInfoFromDatabase(){

    }
}
