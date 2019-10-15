package com.example.timetableapp;

import android.content.Context;
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
    TablePagerAdapter(FragmentManager fm, Context context){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        String weekday = WEEKDAYS[position];
        Bundle arguments = new Bundle();
        arguments.putString(TableFragment.WEEKDAY, weekday);

        TableFragment fragment = new TableFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
