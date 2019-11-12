package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        final Button showButton = view.findViewById(R.id.showInMap);
        showButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(null);
                DialogFragment dialogFragment = map.newInstance(mContext);
                ft.show(dialogFragment);
            }
        });
    }
}
