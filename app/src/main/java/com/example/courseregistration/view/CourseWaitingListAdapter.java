package com.example.courseregistration.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.courseregistration.R;
import com.example.courseregistration.model.CourseWaitingList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseWaitingListAdapter extends RecyclerView.Adapter<CourseWaitingListAdapter.MyViewHolder>{

    private final List<CourseWaitingList> courseWaitingLists;
    private Context context;

    //View holder class
    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView waitListRowCourse;
        final TextView dot;
        final TextView timestamp;

        // view holder constructor
        MyViewHolder(View view) {
            super(view);
            waitListRowCourse = view.findViewById(R.id.waitListRowCourse);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    //adapter constructor
    public CourseWaitingListAdapter(Context context, List<CourseWaitingList> courseWaitingLists) {
        this.courseWaitingLists = courseWaitingLists;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.course_row, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        CourseWaitingList courseWaitingList = courseWaitingLists.get(position);

        myViewHolder.waitListRowCourse.setText(courseWaitingList.getCourse());

        // Displaying dot from HTML character code
        myViewHolder.dot.setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));

        // Formatting and displaying timestamp
        myViewHolder.timestamp.setText(formatDate(courseWaitingList.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return courseWaitingLists.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: 02/21/2018
     */
    private String formatDate(String dateStr) {

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(dateStr);
            return new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}