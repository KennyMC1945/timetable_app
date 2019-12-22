package com.example.timetableapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TableFragment extends Fragment {
    public static final String WEEKDAY = "День недели";
    public static final String CONTENT = "Содержимое";
    public View thisView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        thisView = inflater.inflate(R.layout.fragment_table,container,false);

        Bundle arguments = getArguments();
        if (arguments != null){
            String weekday = arguments.getString(WEEKDAY);
            TextView tv_weekday = thisView.findViewById(R.id.tv_weekday);
            tv_weekday.setText(weekday.substring(0,1).toUpperCase()+weekday.substring(1));
            if (arguments.containsKey(CONTENT)){
                updateContents(TimetableParser.parseTimetable(arguments.getString(CONTENT)),thisView);
            }
        }
        return thisView;
    }





    private void updateContents(ArrayList<LessonInfo> lessons, View view){
        TextView[] titles = {view.findViewById(R.id.tv_firstLesson), view.findViewById(R.id.tv_secondLesson),
                view.findViewById(R.id.tv_thirdLesson), view.findViewById(R.id.tv_fourthLesson), view.findViewById(R.id.tv_fifthLesson)};
        TextView[] auds = {view.findViewById(R.id.tv_firstAud), view.findViewById(R.id.tv_secondAud),
                view.findViewById(R.id.tv_thirdAud), view.findViewById(R.id.tv_fourthAud), view.findViewById(R.id.tv_fifthAud)};
        TextView[] types = {view.findViewById(R.id.tv_firstType), view.findViewById(R.id.tv_secondType),
                view.findViewById(R.id.tv_thirdType), view.findViewById(R.id.tv_fourthType), view.findViewById(R.id.tv_fifthType)};
        for (LessonInfo lessonInfo: lessons){
            if (isTopWeek() && lessonInfo.getWeek() == LessonInfo.BOTTOM_WEEK) continue;
            if (!isTopWeek() && lessonInfo.getWeek() == LessonInfo.TOP_WEEK) continue;
            int number = lessonInfo.getNumber()-1;
            titles[number].setText(lessonInfo.getTitle());
            titles[number].setVisibility(View.VISIBLE);
            auds[number].setText(lessonInfo.getAud());
            auds[number].setVisibility(View.VISIBLE);
            types[number].setText(lessonInfo.getTypeText());
            types[number].setBackgroundResource(getBgByType(lessonInfo.getType()));
            types[number].setVisibility(View.VISIBLE);
        }
    }

    private int getBgByType(int type){
        switch (type) {
            case LessonInfo.LECTURE:
                return R.drawable.lection_icon;
            case LessonInfo.PRACTICE:
                return R.drawable.practice_icon;
            case LessonInfo.LAB:
                return R.drawable.lab_icon;

        }
        return 0;
    }

    private boolean isTopWeek() {
        int top_week = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE).getInt("top_week", 0);
//        Date today = Calendar.getInstance().getTime();
//        SimpleDateFormat weekFormat = new SimpleDateFormat("w");
//        SimpleDateFormat weekdayFormat = new SimpleDateFormat("u");
        int week_now = TimeUtils.getWeek();
        int weekday = TimeUtils.getWeekDay();
        if (weekday > 5) week_now++;
        if ((week_now-top_week)%2 == 0) return true;
        else return false;
    }

}
