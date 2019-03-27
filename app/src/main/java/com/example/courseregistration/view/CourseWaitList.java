package com.example.courseregistration.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.courseregistration.DatabaseHelper;
import com.example.courseregistration.R;
import com.example.courseregistration.model.CourseWaitingList;
import com.example.courseregistration.utils.MyDividerItemDecoration;
import com.example.courseregistration.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseWaitList extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_waitList_view) TextView noWaitListView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private CourseWaitingListAdapter mAdapter;
    private final List<CourseWaitingList> courseWaitingLists = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_wait_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);

        courseWaitingLists.addAll(db.getAllCourseWaitingLists());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitListDialog(false, null, -1);
            }
        });

        //set up adapter and recyclerView
        mAdapter = new CourseWaitingListAdapter(this, courseWaitingLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyWaitList();

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         *
         * On short press open that courses waitlist
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                String tableName = courseWaitingLists.get(position).getCourse();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("CourseTable", tableName);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        //check if first use of app
        SharedPreferences sharedpreferences = getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        String initial = sharedpreferences.getString("initial", null);

        //if first use add courses
        if (initial == null) {
            createWaitList("Network Security");
            createWaitList("Database Systems");
            createWaitList("Web Development");
            createWaitList("Human Computer Interaction");
            createWaitList("Machine Learning");
            createWaitList("Mobile Computing");

            SharedPreferences.Editor editor = getSharedPreferences("firstTime", MODE_PRIVATE).edit();
            editor.putString("initial", "Done");
            editor.apply();
        }
    }

    /**
     * Inserting new course in db
     * and refreshing the list
     */
    private void createWaitList(String course) {
        // inserting course in db and getting
        // newly inserted course id
        long id = db.insertCourse(course);

        // get the newly inserted course from db
        CourseWaitingList courseWaitingList = db.getCourseWaitingList(id);

        if (courseWaitingList != null) {
            // adding new course to array list at 0 position
            courseWaitingLists.add(0, courseWaitingList);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyWaitList();
        }
    }

    /**
     * Updating course in db and updating
     * item in the list by its position
     */
    private void updateWaitList(String course, int position) {
        CourseWaitingList courseWaitingList = courseWaitingLists.get(position);
        String OldTableName = courseWaitingList.getCourse();

        // updating course name
        courseWaitingList.setCourse(course);

        // updating course in db
        db.updateCourseWaitingList(courseWaitingList, OldTableName);

        // refreshing the list
        courseWaitingLists.set(position, courseWaitingList);
        mAdapter.notifyItemChanged(position);

        toggleEmptyWaitList();
    }

    /**
     * Deleting course from SQLite and removing the
     * item from the list by its position
     */
    private void deleteCourse(int position) {
        // deleting the course from db
        db.deleteCourseWaitingList(courseWaitingLists.get(position), courseWaitingLists.get(position).getCourse());

        // removing the course from the list
        courseWaitingLists.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyWaitList();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 1
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showWaitListDialog(true, courseWaitingLists.get(position), position);
                } else {
                    deleteCourse(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a course.
     * when shouldUpdate=true, it automatically displays old course and changes the
     * button text to UPDATE
     */
    private void showWaitListDialog(final boolean shouldUpdate, final CourseWaitingList courseWaitingList, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.course_dialog, null, false);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(CourseWaitList.this);
        alertDialogBuilderUserInput.setView(view);

        final TextInputEditText inputCourse = view.findViewById(R.id.dialog_course);
        final TextInputLayout inputCourseLayout = view.findViewById(R.id.dialog_course_text_input_layout);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);

        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_course_title) : getString(R.string.lbl_edit_course_title));

        if (shouldUpdate && courseWaitingList != null) {
            inputCourse.setText(courseWaitingList.getCourse());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean cancel = false;
                View focusView = null;

                //set all error to null
                inputCourseLayout.setError(null);

                //store value at the time of the registration attempt
                String course = Objects.requireNonNull(inputCourse.getText()).toString();

                // Validate course input
                // Check if empty
                if (TextUtils.isEmpty(course)) {
                    inputCourseLayout.setError(getString(R.string.error_field_required));
                    focusView = inputCourse;
                    cancel = true;
                }

                //if no errors found update/create student else set focus on first error
                if (cancel) {
                    focusView.requestFocus();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating student
                if (shouldUpdate && courseWaitingList != null) {
                    // update student by it's id
                    updateWaitList(course, position);
                } else {
                    // create new student
                    createWaitList(course);
                }
            }
        });
    }

    /**
     * Toggling list and empty course view
     */
    private void toggleEmptyWaitList() {
        // you can check courseList.size() > 0
        if (db.getCourseWaitingListsCount() > 0) {
            noWaitListView.setVisibility(View.GONE);
        } else {
            noWaitListView.setVisibility(View.VISIBLE);
        }
    }
}
