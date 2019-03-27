package com.example.courseregistration.view;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.example.courseregistration.R;
import com.example.courseregistration.DatabaseHelper;
import com.example.courseregistration.model.StudentWaitingList;
import com.example.courseregistration.utils.MyDividerItemDecoration;
import com.example.courseregistration.utils.RecyclerTouchListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_waitList_view) TextView noWaitListView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.sort_spinner) Spinner spinner;

    private StudentWaitingListAdapter mAdapter;
    private final List<StudentWaitingList> studentWaitingLists = new ArrayList<>();
    private DatabaseHelper db;
    private String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();//receiving the intent send by the CourseWaitList activity
        tableName = intent.getStringExtra("CourseTable");//converting that intent message to string using the getStringExtra() method

        db = new DatabaseHelper(this);
        studentWaitingLists.addAll(db.getAllStudentWaitingLists(tableName));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitListDialog(false, null, -1);
            }
        });

        //setup adapter and recyclerView
        mAdapter = new StudentWaitingListAdapter(this, studentWaitingLists);
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
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));


        setupSort();
    }

    /**
     * Inserting new student in db
     * and refreshing the list
     */
    private void createWaitList(String firstName, String lastName, int priority) {
        // inserting student in db and getting
        // newly inserted student id
        long id = db.insertStudent(firstName, lastName, priority, tableName);

        // get the newly inserted student from db
        StudentWaitingList studentWaitingList= db.getStudentWaitingList(id, tableName);

        if (studentWaitingList != null) {
            // adding new student to array list at 0 position
            studentWaitingLists.add(0, studentWaitingList);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyWaitList();
        }
    }

    /**
     * Updating student in db and updating
     * item in the list by its position
     */
    private void updateWaitList(String studentFirstName, String studentLastName, int priority, int position) {
        StudentWaitingList studentWaitingList = studentWaitingLists.get(position);
        // updating student info
        studentWaitingList.setStudentFirstName(studentFirstName);
        studentWaitingList.setStudentLastName(studentLastName);
        studentWaitingList.setPriority(priority);

        // updating student in db
        db.updateStudentWaitingList(studentWaitingList, tableName);

        // refreshing the list
        studentWaitingLists.set(position, studentWaitingList);
        mAdapter.notifyItemChanged(position);

        toggleEmptyWaitList();
    }

    /**
     * Deleting student from SQLite and removing the
     * item from the list by its position
     */
    private void deleteStudent(int position) {
        // deleting the student from db
        db.deleteStudentWaitingList(studentWaitingLists.get(position), tableName);

        // removing the student from the list
        studentWaitingLists.remove(position);
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
                    showWaitListDialog(true, studentWaitingLists.get(position), position);
                } else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a student.
     * when shouldUpdate=true, it automatically displays old student and changes the
     * button text to UPDATE
     */
    private void showWaitListDialog(final boolean shouldUpdate, final StudentWaitingList studentWaitingList, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.wait_list_dialog, null, false);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final TextInputEditText inputStudentFirstName = view.findViewById(R.id.dialog_studentFirstName);
        final TextInputEditText inputStudentLastName = view.findViewById(R.id.dialog_studentLastName);
        final Spinner inputPriority = view.findViewById(R.id.dialog_priority);
        final TextInputLayout inputStudentFirstNameLayout = view.findViewById(R.id.dialog_studentFirstName_text_input_layout);
        final TextInputLayout inputStudentLastNameLayout = view.findViewById(R.id.dialog_studentLastName_text_input_layout);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);

        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_student_title) : getString(R.string.lbl_edit_student_title));

        if (shouldUpdate && studentWaitingList != null) {
            inputStudentFirstName.setText(studentWaitingList.getStudentFirstName());
            inputStudentLastName.setText(studentWaitingList.getStudentLastName());
            inputPriority.setSelection(studentWaitingList.getPriority());
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
                inputStudentFirstNameLayout.setError(null);
                inputStudentLastNameLayout.setError(null);

                //store value at the time of the registration attempt
                String firstName = Objects.requireNonNull(inputStudentFirstName.getText()).toString();
                String lastName = Objects.requireNonNull(inputStudentLastName.getText()).toString();
                int priority = inputPriority.getSelectedItemPosition();

                //validate student first name input
                //check if empty
                if (TextUtils.isEmpty(firstName)) {
                    inputStudentFirstNameLayout.setError(getString(R.string.error_field_required));
                    focusView = inputStudentFirstName;
                    cancel = true;
                }

                //validate student last name input
                //check if empty
                if (TextUtils.isEmpty(lastName)) {
                    inputStudentLastNameLayout.setError(getString(R.string.error_field_required));
                    focusView = inputStudentLastName;
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
                if (shouldUpdate && studentWaitingList != null) {
                    // update student by it's id
                    updateWaitList(firstName, lastName, priority, position);
                    updateSort();
                } else {
                    // create new student
                    createWaitList(firstName, lastName, priority);
                    updateSort();
                }
            }
        });
    }

    /**
     * Toggling list and empty students view
     */
    private void toggleEmptyWaitList() {
        // you can check studentsList.size() > 0
        if (db.getStudentWaitingListsCount(tableName) > 0) {
            noWaitListView.setVisibility(View.GONE);
        } else {
            noWaitListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //setup sort list spinner
    //on select change sort type
    private void setupSort() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sort_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                updateSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateSort() {

        int position = spinner.getSelectedItemPosition();

        // 0 is name
        // 1 is priority
        if (position == 0) {
            sortByDate();
        } else if (position == 1) {
            sortByName();
        } else {
            sortByPriority();
        }

        mAdapter.notifyDataSetChanged();
    }

    //sort by lastName a to z
    private void sortByName() {
        Collections.sort(studentWaitingLists,  (l1, l2) -> l1.getStudentLastName().compareTo(l2.getStudentLastName()));
    }

    //sort by priority highest to lowest
    private void sortByPriority() {
        Collections.sort(studentWaitingLists, (l1, l2) -> {
            if (l1.getPriority() > l2.getPriority()) {
                return 1;
            } else if (l1.getPriority() < l2.getPriority()) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    private void sortByDate() {
        Collections.sort(studentWaitingLists, new DateTimeComparator());
    }

    // Class to compare date and time so that items are sorted in descending order
    public class DateTimeComparator implements Comparator {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        public int compare(Object a, Object b) {
            String o1 = ((StudentWaitingList)a).getTimestamp();
            String o2 = ((StudentWaitingList)b).getTimestamp();

            try {
                return dateFormat.parse(o2).compareTo(dateFormat.parse(o1));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}