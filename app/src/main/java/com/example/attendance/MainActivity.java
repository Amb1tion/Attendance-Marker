package com.example.attendance;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AppDatabase appdb;
    private List<Student> studentlist;
    private StudentListAdapter adapter;
    private ListView mListView;
    private String section;
    private String course;
    private Attendance inst_att;
    private FloatingActionButton fab;
    private Date date;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> lect_adapter;
    private String attendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        getSupportActionBar().setLogo(R.drawable.logo);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"OnCreate: Started.");
        final Intent intent = getIntent();
        section = intent.getStringExtra("section");
        course = intent.getStringExtra("course");
        appdb = AppDatabase.getInstance(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView);
        if (intent.hasExtra("attendance")){
            attendance = intent.getStringExtra("attendance");
            Gson gson = new Gson();
            Type type = new TypeToken<Attendance>() {
            }.getType();
            Attendance productCategoriesList = gson.fromJson(attendance, type);
            studentlist = productCategoriesList.getStudentlist();
            adapter = new StudentListAdapter(MainActivity.this,R.layout.adapter_view_layout,studentlist);
            mListView.setAdapter(adapter);

        }
        else{ displayList();}
        View fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(studentlist!= null && !(intent.hasExtra("attendance"))){
                    date = new Date();
                    LayoutInflater li = LayoutInflater.from(MainActivity.this);
                    View promptsView = li.inflate(R.layout.att_prompt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);
                    alertDialogBuilder.setView(promptsView);

                    alertDialogBuilder.setTitle("Select Number of Lectures");
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner);
                    final Button mButton =  (Button) promptsView.findViewById((R.id.button2));
                    arrayList= new ArrayList<String>();
                    arrayList.add("1");
                    arrayList.add("2");
                    arrayList.add("3");
                    lect_adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,arrayList);
                    mSpinner.setAdapter(lect_adapter);
                    alertDialog.show();
                    mButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedItem =  (String) mSpinner.getSelectedItem().toString();
                            inst_att = new Attendance(date,course,studentlist,section,selectedItem);
                            new InsertTask(MainActivity.this,inst_att).execute();
                        }
                    });
                }
            }
        });

    }
    private static class InsertTask extends AsyncTask<Void,Void,Boolean>{
        private Attendance attendance;
        private WeakReference<MainActivity> activityReference;
        InsertTask(MainActivity context,Attendance attendance){
            activityReference=new WeakReference<>(context);
            this.attendance = attendance;
        }
        @Override
        protected Boolean doInBackground(Void... objs){
            if(activityReference!=null){
                activityReference.get().appdb.attendanceDao().insert(attendance);
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            if(bool){
                activityReference.get().setResult();

            }
        }

    }
    private void setResult(){
        Intent intent = new Intent(getBaseContext(),attendance_activity.class);
        intent.putExtra("course",course);
        startActivity(intent);
        finish();

    }
    private void displayList(){
// initialize database instance
//        appdb = AppDatabase.getInstance(MainActivity.this);
// fetch list of notes in background thread
        new RetrieveTask(this).execute();
    }
    private static class RetrieveTask extends AsyncTask<Void,Void, List<Student>>{

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        RetrieveTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Student> doInBackground(Void... voids) {
            if (activityReference.get()!=null)
                return activityReference.get().appdb.studentDao().findBySection(activityReference.get().section);
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Student> studentlist) {
            if (studentlist!=null && studentlist.size()>0 ){
                activityReference.get().studentlist = studentlist;

                // hides empty text view
//                activityReference.get().textViewMsg.setVisibility(View.GONE);

                // create and set the adapter on RecyclerView instance to display list
                activityReference.get().adapter = new StudentListAdapter(activityReference.get(),R.layout.adapter_view_layout,studentlist);
                activityReference.get().mListView.setAdapter(activityReference.get().adapter);

            }
        }


    }

    }
