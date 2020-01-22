package com.example.attendance;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 3/14/2017.
 */

public class AttendanceListAdapter extends ArrayAdapter<Attendance> {

    private static final String TAG = "StudentListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Date;
        TextView Lectures;
        TextView Section;
    }

    /**
     * Default constructor for the StudentListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public AttendanceListAdapter(Context context, int resource, List<Attendance> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        List attendanceList =  objects;
    }

    //    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        final Attendance temp = getItem(position);

        Date  date = temp.getDate();
        String course = temp.getCourse();
        String section = temp.getSection();
        String lectures = temp.getLectures();
        List<Student> studentlist = temp.getStudentlist();
        Attendance attinst = new Attendance(date,course,studentlist,section,lectures);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        final ViewHolder holder;

        //if view is being rendered for the first time then the getview object will be passed convertview = null so we intialize the viewholder object
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.Date = (TextView) convertView.findViewById(R.id.textView1);
            holder.Lectures = (TextView) convertView.findViewById(R.id.textView2);
            holder.Section = (TextView) convertView.findViewById(R.id.textView3);

            result = convertView;
            //set tag stores the convertView in memory
            convertView.setTag(holder);
        }
        else{
            //get tag reads the view from memory so that we don't have to create unnecessary objects for each render
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");

        holder.Date.setText(ft.format(attinst.getDate()));
        holder.Lectures.setText(attinst.getLectures());
        holder.Section.setText(attinst.getSection());


        return convertView;
    }
}