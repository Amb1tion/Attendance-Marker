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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 3/14/2017.
 */

public class StudentListAdapter extends ArrayAdapter<Student> {

    private static final String TAG = "StudentListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        TextView rollno;
        Button attendance;
    }

    /**
     * Default constructor for the StudentListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public StudentListAdapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        List studentList =  objects;
    }

//    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        final Student temp = getItem(position);
        String name = temp.getName();
        String rollno = temp.getRollno();
        String attendance = temp.getAttendance();
        String section = temp.getSection();

        //Create the person object with the information
        Student student = new Student(name,rollno,attendance,section);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        final ViewHolder holder;

        //if view is being rendered for the first time then the getview object will be passed convertview = null so we intialize the viewholder object
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.textView1);
            holder.rollno = (TextView) convertView.findViewById(R.id.textView2);
            holder.attendance = (Button) convertView.findViewById(R.id.Button);

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

        holder.name.setText(student.getName());
        holder.rollno.setText(student.getRollno());
        holder.attendance.setText(student.getAttendance());

        holder.attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,temp.getAttendance());
                if (temp.getAttendance() == "Present"){
                    temp.setAttendance("Absent");
                    holder.attendance.setText("Absent");
                    Log.e(TAG,temp.getAttendance());
                }
                else{
                    temp.setAttendance("Present");
                    holder.attendance.setText("Present");
                }
            }
        });


        return convertView;
    }
}