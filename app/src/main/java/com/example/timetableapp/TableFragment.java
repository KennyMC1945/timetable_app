package com.example.timetableapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class TableFragment extends Fragment {
    public static final String WEEKDAY = "День недели";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_table,container,false);

        Bundle arguments = getArguments();
        if (arguments != null){
            String weekday = arguments.getString(WEEKDAY);
            TextView tv_weekday = view.findViewById(R.id.tv_weekday);
            tv_weekday.setText(weekday.substring(0,1).toUpperCase()+weekday.substring(1));
        }
        return view;
    }
}
