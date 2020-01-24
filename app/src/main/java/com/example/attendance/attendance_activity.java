package com.example.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class attendance_activity extends AppCompatActivity {
    private static final String TAG = "attendance_activity";
    private AppDatabase appDatabase;
    private String course;
    private ListView mListView;
    private FloatingActionButton fab;
    private ArrayAdapter<String> adapter;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.attendance";
    private ArrayList<String> sections;
    private ArrayList<String> empty;
    private Boolean flag=false;
    private ArrayAdapter<String> section_adapter;
    private List<Attendance> att_list;
    private AttendanceListAdapter attendancelistadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_act_layout);
        appDatabase = AppDatabase.getInstance(attendance_activity.this);
        mPreferences = attendance_activity.this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        mListView = (ListView) findViewById(R.id.listView2);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,View view,int position, long id){
                Attendance selectedItem = (Attendance) parent.getItemAtPosition(position);
                Gson gson = new Gson();
                Type type = new TypeToken<Attendance>() {
                }.getType();
                String json = gson.toJson(selectedItem, type);
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                intent.putExtra("attendance",json);
                startActivity(intent);
                return true;
            }
        });

        try {
            sections = (ArrayList<String>) ObjectSerializer.deserialize(mPreferences.getString("Sections", ObjectSerializer.serialize(new ArrayList<String>())));
            if (sections == null || sections.size() == 0){
                empty = new ArrayList<String>();
                this.empty.add("No Sections Added");
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        course = getIntent().getStringExtra("course");
        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LayoutInflater li = LayoutInflater.from(attendance_activity.this);
                View promptsView = li.inflate(R.layout.section_prompt,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(attendance_activity.this);
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setTitle("Select Section");
                final AlertDialog alertDialog = alertDialogBuilder.create();
                final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner);
                final Button mButton =  (Button) promptsView.findViewById((R.id.button2));
                if (flag){section_adapter = new ArrayAdapter<String>(attendance_activity.this,android.R.layout.simple_spinner_item,empty);}
                else{section_adapter = new ArrayAdapter<String>(attendance_activity.this,android.R.layout.simple_spinner_item,sections);}
                section_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                mSpinner.setAdapter(section_adapter);
                alertDialog.show();
                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(TAG,"i'm stuff");
                        Intent intent = new Intent(getBaseContext(),MainActivity.class);
                        String selectedItem = (String) mSpinner.getSelectedItem().toString();
                        intent.putExtra("section",selectedItem);
                        intent.putExtra("course",course);
                        startActivity(intent);
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        });
        displayAttendance();
    }
    private void displayAttendance(){
        new RetrieveTask(attendance_activity.this).execute();
    }
    private static class RetrieveTask extends AsyncTask<Void,Void, List<Attendance>>{
        private WeakReference<attendance_activity> activityReference;
        RetrieveTask(attendance_activity context){activityReference=new WeakReference<>(context);}

        @Override
        protected List<Attendance> doInBackground(Void... voids) {
            if(activityReference.get() != null)
                return activityReference.get().appDatabase.attendanceDao().attendance_instance(activityReference.get().course);
            else
                return null;
        }
        @Override
        protected void onPostExecute(List<Attendance> attendances) {
            activityReference.get().att_list=attendances;
            activityReference.get().attendancelistadapter = new AttendanceListAdapter(activityReference.get(),R.layout.attendance_adapter,attendances);
            activityReference.get().mListView.setAdapter(activityReference.get().attendancelistadapter);
        }


    }
}
