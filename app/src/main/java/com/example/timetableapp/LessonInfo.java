package com.example.timetableapp;

public class LessonInfo {
    /* Константы для недель */
    public static final int TOP_WEEK = 1;
    public static final int BOTTOM_WEEK = -1;
    public static final int EVERY_WEEK = 0;
    /* Константы для типов пар */
    public static final int LECTURE = 101;
    public static final int PRACTICE = 102;
    public static final int LAB = 103;
    // Для обработки ошибок
    public static final int ERROR = -5000;
    /* Информация о паре */
    private int number;
    private int week;
    private int type;
    private String aud;
    private String title;

    public LessonInfo(int number, int week, int type, String aud, String title) {
        this.number = number;
        this.week = week;
        this.type = type;
        this.aud = aud;
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public int getWeek() {
        return week;
    }

    public int getType() {
        return type;
    }

    public String getAud() {
        return aud;
    }

    public String getTitle() {
        return title;
    }

    public String getTypeText(){
        switch (type) {
            case LECTURE:
                return "ЛК";
            case LAB:
                return "ЛР";
            case PRACTICE:
                return "ПР";
        }
        return null;
    }
}
