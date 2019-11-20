package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class searchSensorFragment extends Fragment {
    private ArrayList<SensorInfo> sensors;
    private Context mContext;
    private sensorViewModel viewModel;
    private CheckBox eName;
    private CheckBox eValue;
    private CheckBox eDateFrom;
    private CheckBox eDateTo;
    private EditText name;
    private EditText valMin;
    private EditText valMax;
    private TextView dateMin;
    private TextView dateMax;
    private Button calendarFrom;
    private Button calendarTo;
    private Button searchButton;
    private boolean findName = false;
    private boolean findValue = false;
    private boolean findDateMin = false;
    private boolean findDateMax = false;
    private boolean onShowing = false;
    private Dialog dialog;
    private GridView gridInfo;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int year, month, day, hour, min, second;
    private Calendar calendar;

    searchSensorFragment() {
    }

    searchSensorFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.getSensors().observe(this, item -> {
            sensors = viewModel.getSensors().getValue();
            Log.d("Sensor changed", "Start");
            if (onShowing) {
                try {
                    searching();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.search_result);
        gridInfo = dialog.findViewById(R.id.search_result_grid);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        searchButton = view.findViewById(R.id.searchButton);
        eName = view.findViewById(R.id.enableName);
        name = view.findViewById(R.id.nameToSearch);
        eValue = view.findViewById(R.id.enableValue);
        valMin = view.findViewById(R.id.valueFrom);
        valMax = view.findViewById(R.id.valueTo);
        eDateFrom = view.findViewById(R.id.enableDayFrom);
        eDateTo = view.findViewById(R.id.enableDayTo);
        dateMin = view.findViewById(R.id.dayFrom);
        dateMax = view.findViewById(R.id.dayTo);
        calendarFrom = view.findViewById(R.id.calendarFrom);
        calendarTo = view.findViewById(R.id.calendarTo);
        name.setEnabled(false);
        valMin.setEnabled(false);
        valMax.setEnabled(false);
        dateMin.setEnabled(false);
        dateMax.setEnabled(false);
        calendarFrom.setEnabled(false);
        calendarTo.setEnabled(false);
        eName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Button checked listener","name checked");
                    name.setEnabled(true);
                    findName = true;
                } else {
                    Log.d("Button checked listener","name unchecked");
                    name.setEnabled(false);
                    findName = false;
                }
            }
        });
        eValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Button checked listener","value checked");
                    valMax.setEnabled(true);
                    valMin.setEnabled(true);
                    findValue = true;
                } else {
                    Log.d("Button checked listener","value unchecked");
                    valMax.setEnabled(false);
                    valMin.setEnabled(false);
                    findValue = false;
                }
            }
        });
        eDateTo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Button checked listener","calendarTo checked");
                    calendarTo.setEnabled(true);
                    dateMax.setEnabled(true);
                    findDateMax = true;
                } else {
                    Log.d("Button checked listener","calendarTo unchecked");
                    calendarTo.setEnabled(false);
                    dateMax.setEnabled(false);
                    findDateMax = false;

                }
            }
        });
        eDateFrom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Button checked listener","calendarFrom checked");
                    dateMin.setEnabled(true);
                    calendarFrom.setEnabled(true);
                    findDateMin = true;
                } else {
                    Log.d("Button checked listener","calendarFrom unchecked");
                    calendarFrom.setEnabled(false);
                    dateMin.setEnabled(false);
                    findDateMin = false;

                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    searching();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        DatePickerDialog.OnDateSetListener dateFromListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateMin.setText(dateFormat.format(calendar.getTime()));
            }
        };
        calendarFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, dateFromListener, year, month, day).show();
            }
        });
        DatePickerDialog.OnDateSetListener dateToListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateMax.setText(dateFormat.format(calendar.getTime()));
            }
        };
        calendarTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(mContext, dateToListener, year, month, day).show();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this.getActivity()).get(sensorViewModel.class);
        return inflater.inflate(R.layout.search_sensor, container, false);
    }

    private void searching() throws ParseException {
        //search driver
        ArrayList<SensorInfo> sensorResult = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (SensorInfo sensor : sensors) Log.d("search Sensor", sensor.toString());
        for (SensorInfo sensor : sensors) {
            Log.d("Sensor to test : ",sensor.toString());
            boolean addName = true, addDayMin = true, addDayMax = true, addValue = true;
            if (findName) {
                if (sensor.getName().equals(name.getText().toString())) {
                    Log.d("Search by Name", "Name Search :  " + name.getText().toString() + " Sensor value : " + name.getText().toString());
                    addName = true;
                }
                else addName = false;
            }
            if (findDateMax && findDateMin) {
                Log.d("Date filter", " From " + dateMin.getText().toString() + " to " + dateMax.getText().toString());
                if (sensor.dayWithinRange(formatter.parse(dateMin.getText().toString()), formatter.parse(dateMax.getText().toString())))
                    if (sensor.dayWithinRange(null, formatter.parse(dateMax.getText().toString()))) {
                        Log.d("Search by Max Date", "From " + formatter.parse(dateMin.getText().toString()) + " to "
                                + formatter.parse(dateMax.getText().toString()) + " Sensor value : " + String.valueOf(sensor.getDayAdded()));
                        addDayMax = true; addDayMin = true;
                    }
                else {
                        addDayMax = false; addDayMin = false;
                    }
            } else if (findDateMax) {
                if (sensor.dayWithinRange(null, formatter.parse(dateMax.getText().toString()))) {
                    Log.d("Search by Max Date", "From 1/1/1970" + " to "
                            + formatter.parse(dateMax.getText().toString()) + " Sensor value : " + String.valueOf(sensor.getDayAdded()));
                    addDayMax = true;
                }
                else addDayMax = false;
            } else if (findDateMin) {
                if (sensor.dayWithinRange(formatter.parse(dateMin.getText().toString()), null)) {
                    Log.d("Search by Min Date", "From " + formatter.parse(dateMin.getText().toString()) + " to now"
                            + " Sensor value : " + String.valueOf(sensor.getDayAdded()));
                    addDayMin = true;
                }
                else addDayMin = false;
            }
            if (findValue) {
                if (sensor.valueWithinRange(Double.parseDouble(valMin.getText().toString()), Double.parseDouble(valMax.getText().toString()))) {
                    Log.d("Search by Value", "From " + Double.parseDouble(valMin.getText().toString()) + " to "
                            + Double.parseDouble(valMax.getText().toString()) + " Sensor value : " + String.valueOf(sensor.getValue()));
                    addValue = true;
                }
                else addValue = false;
            }
            if(addDayMax&&addDayMin&&addName&&addDayMin&&addValue) sensorResult.add(sensor);
        }
        Log.d("Search Sensor Result", String.valueOf(sensorResult.size()));
        addToDialog(sensorResult);
    }

    private void addToDialog(ArrayList<SensorInfo> sensorsResult) {
        if (dialog.isShowing()) dialog.dismiss();
        addToGrid(sensorsResult);
        onShowing = true;
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onShowing = false;
            }
        });
    }

    private void addToGrid(ArrayList<SensorInfo> sensorResult) {
        gridInfo.setAdapter(new sensorAdapter(mContext, sensorResult));
    }
}
