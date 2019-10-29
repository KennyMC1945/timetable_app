package com.example.timetableapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TablePagerAdapter extends FragmentPagerAdapter {

    public final String[] WEEKDAYS = {"понедельник","вторник","среда","четверг","пятница"};
    private Context context;
    private SharedPreferences sharedPrefs;
    TablePagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context=context;
        sharedPrefs = context.getSharedPreferences("timetable",Context.MODE_PRIVATE);
    }

    @Override
    public Fragment getItem(int position) {
        String weekday = WEEKDAYS[position];
        String content = getContent(position);
        Bundle arguments = new Bundle();
        arguments.putString(TableFragment.WEEKDAY, weekday);
        if (content != null) arguments.putString(TableFragment.CONTENT,content);
        TableFragment fragment = new TableFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }
    private String getContent(int position){
        switch (position) {
            case 0:
                return sharedPrefs.getString("mon",null);
            case 1:
                return sharedPrefs.getString("tue",null);
            case 2:
                return sharedPrefs.getString("wed",null);
            case 3:
                return sharedPrefs.getString("thu",null);
            case 4:
                return sharedPrefs.getString("fri",null);
        }
        return null;
    }
}
