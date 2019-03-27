package com.example.courseregistration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.courseregistration.model.StudentWaitingList;
import com.example.courseregistration.model.CourseWaitingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 11;
    // Database Name
    private static final String DATABASE_NAME = "waitingList_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create waitingList table
        db.execSQL(CourseWaitingList.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CourseWaitingList.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insertStudent(String studentFirstName, String studentLastName, int priority, String tableName) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(StudentWaitingList.COLUMN_StudentFirstName, studentFirstName);
        values.put(StudentWaitingList.COLUMN_StudentLastName, studentLastName);
        values.put(StudentWaitingList.COLUMN_Priority, priority);

        // insert row
        long id = db.insert("'" + tableName + "'", null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertCourse(String course) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(CourseWaitingList.COLUMN_Course, course);

        // insert row
        long id = db.insert(CourseWaitingList.TABLE_NAME, null, values);

        // Create table SQL query
        String CREATE_TABLE =
                "CREATE TABLE '" + course + "' ("
                        + StudentWaitingList.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + StudentWaitingList.COLUMN_StudentFirstName + " TEXT,"
                        + StudentWaitingList.COLUMN_StudentLastName + " TEXT,"
                        + StudentWaitingList.COLUMN_Priority + " INTEGER,"
                        + StudentWaitingList.COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")";

        db.execSQL("DROP TABLE IF EXISTS '" + course + "'");
        db.execSQL(CREATE_TABLE);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public StudentWaitingList getStudentWaitingList(long id, String tableName) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("'" + tableName + "'",
                new String[]{StudentWaitingList.COLUMN_ID, StudentWaitingList.COLUMN_StudentFirstName, StudentWaitingList.COLUMN_StudentLastName,
                        StudentWaitingList.COLUMN_Priority, StudentWaitingList.COLUMN_TIMESTAMP},StudentWaitingList.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare WaitingList object
        StudentWaitingList studentWaitingList = new StudentWaitingList(
                cursor.getInt(Objects.requireNonNull(cursor).getColumnIndex(StudentWaitingList.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_StudentFirstName)),
                cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_StudentLastName)),
                cursor.getInt(cursor.getColumnIndex(StudentWaitingList.COLUMN_Priority)),
                cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();
        db.close();

        return studentWaitingList;
    }

    public CourseWaitingList getCourseWaitingList(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(CourseWaitingList.TABLE_NAME,
                new String[]{CourseWaitingList.COLUMN_ID, CourseWaitingList.COLUMN_Course, CourseWaitingList.COLUMN_TIMESTAMP},CourseWaitingList.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare WaitingList object
        CourseWaitingList courseWaitingList = new CourseWaitingList(
                cursor.getInt(Objects.requireNonNull(cursor).getColumnIndex(CourseWaitingList.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(CourseWaitingList.COLUMN_Course)),
                cursor.getString(cursor.getColumnIndex(CourseWaitingList.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();
        db.close();

        return courseWaitingList;
    }

    public List<StudentWaitingList> getAllStudentWaitingLists(String tableName) {
        List<StudentWaitingList> studentWaitingLists = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM '" + tableName + "' ORDER BY " +
                StudentWaitingList.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StudentWaitingList studentWaitingList = new StudentWaitingList();
                studentWaitingList.setId(cursor.getInt(cursor.getColumnIndex(StudentWaitingList.COLUMN_ID)));
                studentWaitingList.setStudentFirstName(cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_StudentFirstName)));
                studentWaitingList.setStudentLastName(cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_StudentLastName)));
                studentWaitingList.setPriority(cursor.getInt(cursor.getColumnIndex(StudentWaitingList.COLUMN_Priority)));
                studentWaitingList.setTimestamp(cursor.getString(cursor.getColumnIndex(StudentWaitingList.COLUMN_TIMESTAMP)));

                studentWaitingLists.add(studentWaitingList);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // close db connection
        db.close();

        // return WaitingLists list
        return studentWaitingLists;
    }

    public List<CourseWaitingList> getAllCourseWaitingLists() {
        List<CourseWaitingList> courseWaitingLists = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + CourseWaitingList.TABLE_NAME + " ORDER BY " +
                CourseWaitingList.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CourseWaitingList courseWaitingList = new CourseWaitingList();
                courseWaitingList.setId(cursor.getInt(cursor.getColumnIndex(courseWaitingList.COLUMN_ID)));
                courseWaitingList.setCourse(cursor.getString(cursor.getColumnIndex(courseWaitingList.COLUMN_Course)));
                courseWaitingList.setTimestamp(cursor.getString(cursor.getColumnIndex(courseWaitingList.COLUMN_TIMESTAMP)));

                courseWaitingLists.add(courseWaitingList);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // close db connection
        db.close();

        // return WaitingLists list
        return courseWaitingLists;
    }

    public int getStudentWaitingListsCount(String tableName) {
        String countQuery = "SELECT * FROM '" + tableName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

    public int getCourseWaitingListsCount() {
        String countQuery = "SELECT * FROM " + CourseWaitingList.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return count;
    }

    public void updateStudentWaitingList(StudentWaitingList studentWaitingList, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(StudentWaitingList.COLUMN_StudentFirstName, studentWaitingList.getStudentFirstName());
        values.put(StudentWaitingList.COLUMN_StudentLastName, studentWaitingList.getStudentLastName());
        values.put(StudentWaitingList.COLUMN_Priority, studentWaitingList.getPriority());

        // updating row
        db.update("'" + tableName + "'", values, StudentWaitingList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentWaitingList.getId())});

        db.close();
    }

    public void updateCourseWaitingList(CourseWaitingList courseWaitingList, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CourseWaitingList.COLUMN_Course, courseWaitingList.getCourse());

        // updating row
        db.update(CourseWaitingList.TABLE_NAME, values, CourseWaitingList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(courseWaitingList.getId())});

        String alterQuery = "ALTER TABLE '" + tableName + "' RENAME TO '" + courseWaitingList.getCourse() + "'";
        Cursor cursor = db.rawQuery(alterQuery, null);

        cursor.close();
        db.close();
    }

    public void deleteStudentWaitingList(StudentWaitingList studentWaitingList, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("'" + tableName + "'", StudentWaitingList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentWaitingList.getId())});
        db.close();
    }

    public void deleteCourseWaitingList(CourseWaitingList courseWaitingList, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CourseWaitingList.TABLE_NAME, CourseWaitingList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(courseWaitingList.getId())});

        db.execSQL("DROP TABLE IF EXISTS '" + tableName + "'");
        db.close();
    }
}