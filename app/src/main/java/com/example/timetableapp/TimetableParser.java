package com.example.timetableapp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimetableParser {

    public static ArrayList<LessonInfo> parseTimetable(String dayInfo){
        ArrayList<LessonInfo> resultArray = new ArrayList<>();
        String[] lessons = dayInfo.split(";");
        Pattern typePattern = Pattern.compile("-.+?-");
        Pattern titlePattern = Pattern.compile(":.+?-");
        Pattern numberPattern = Pattern.compile("\\|\\d?:");
        Pattern audPattern = Pattern.compile("\\[.+?\\]");
        for (String lesson:lessons){
            Matcher typeMatcher = typePattern.matcher(lesson);
            Matcher titleMatcher = titlePattern.matcher(lesson);
            Matcher numberMatcher = numberPattern.matcher(lesson);
            Matcher audMatcher = audPattern.matcher(lesson);
            typeMatcher.find();
            titleMatcher.find();
            numberMatcher.find();
            audMatcher.find();
            int week = getWeek(lesson.substring(0,lesson.indexOf("|")));
            int type = getType(lesson.substring(typeMatcher.start()+1,typeMatcher.end()-1));
            int number = Integer.parseInt(lesson.substring(numberMatcher.start()+1,numberMatcher.end()-1));
            String title = lesson.substring(titleMatcher.start()+1,titleMatcher.end()-1);
            String aud = lesson.substring(audMatcher.start()+1,audMatcher.end()-1);
            LessonInfo lessonInfo = new LessonInfo(number,week,type,aud,title);
            resultArray.add(lessonInfo);
        }
        return resultArray;
    }

    private static int getType(String type){
        switch (type){
            case "пр.з":
                return LessonInfo.PRACTICE;
            case "лекция":
                return LessonInfo.LECTURE;
            case "лаб":
                return LessonInfo.LAB;
        }
        return LessonInfo.ERROR;
    }

    private static int getWeek(String week){
        switch (week){
            case "T":
                return LessonInfo.TOP_WEEK;
            case "B":
                return LessonInfo.BOTTOM_WEEK;
            case "E":
                return LessonInfo.EVERY_WEEK;
        }
        return LessonInfo.ERROR;
    }

}
