package com.example.courseregistration.model;

public class CourseWaitingList {
    public static final String TABLE_NAME = "CourseWaitingList";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_Course = "Course";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String course;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_Course + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public CourseWaitingList() {
    }

    //constructor
    public CourseWaitingList(int id, String course, String timestamp) {
        this.id = id;
        this.course = course;
        this.timestamp = timestamp;
    }

    //gets and sets for each aspect
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
