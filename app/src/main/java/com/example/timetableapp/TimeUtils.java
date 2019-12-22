package com.example.timetableapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static int getWeek(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat weekFormat = new SimpleDateFormat("w");
        return Integer.parseInt(weekFormat.format(today));
    }

    public static int getWeekDay(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("u");
        return Integer.parseInt(weekdayFormat.format(today));
    }
}
