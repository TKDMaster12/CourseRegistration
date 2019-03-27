package com.example.courseregistration.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.courseregistration.R;
import com.example.courseregistration.model.StudentWaitingList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentWaitingListAdapter extends RecyclerView.Adapter<StudentWaitingListAdapter.MyViewHolder>{

    private final List<StudentWaitingList> studentWaitingLists;
    private Context context;

    //view holder class
    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView waitListRowStudent;
        final TextView waitListRowPriority;
        final TextView dot;
        final TextView timestamp;

        //view holder constructor
        MyViewHolder(View view) {
            super(view);
            waitListRowStudent = view.findViewById(R.id.waitListRowStudentName);
            waitListRowPriority = view.findViewById(R.id.waitListRowPriority);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    //adapter constructor
    public StudentWaitingListAdapter(Context context, List<StudentWaitingList> studentWaitingLists) {
        this.studentWaitingLists = studentWaitingLists;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.wait_list_row, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        StudentWaitingList studentWaitingList = studentWaitingLists.get(position);

        String priorityText = "";

        //get priority number get text for that number
        switch (studentWaitingList.getPriority()) {
            case 0:
                priorityText = "Graduate";
                break;
            case 1:
                priorityText = "4th year";
                break;
            case 2:
                priorityText = "3rd year";
                break;
            case 3:
                priorityText = "2nd year";
                break;
            case 4:
                priorityText = "1st year";
                break;
        }

        String studentName = studentWaitingList.getStudentFirstName() + " " + studentWaitingList.getStudentLastName();
        myViewHolder.waitListRowStudent.setText(studentName);
        myViewHolder.waitListRowPriority.setText(priorityText);

        // Displaying dot from HTML character code
        myViewHolder.dot.setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));

        // Formatting and displaying timestamp
        myViewHolder.timestamp.setText(formatDate(studentWaitingList.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return studentWaitingLists.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: 02/21/2018 12:15 am
     */
    private String formatDate(String dateStr) {

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateStr);
            return new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}