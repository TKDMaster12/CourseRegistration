package com.example.courseregistration.model;

public class StudentWaitingList {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_StudentFirstName = "StudentFirstName";
    public static final String COLUMN_StudentLastName = "StudentLastName";
    public static final String COLUMN_Priority = "Priority";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String studentFirstName;
    private String studentLastName;
    private int priority;
    private String timestamp;

    public StudentWaitingList() {
    }

    //constructor
    public StudentWaitingList(int id, String studentFirstName, String studentLastName, int priority, String timestamp) {
        this.id = id;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    //gets and sets for each aspect
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
