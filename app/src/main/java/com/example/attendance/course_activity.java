package com.example.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class course_activity extends AppCompatActivity {
    private static final String TAG = "course";
    private AppDatabase appdb;
    private ArrayAdapter<String> adapter;
    private ListView mListView;
    private List<String> courses;
    private FloatingActionButton fab;
    private String course_code;
    private String name;
    private AppDatabase appDatabase;
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(" Courses");
        getSupportActionBar().setLogo(R.drawable.logo);
        setContentView(R.layout.course_layout);
        appDatabase= AppDatabase.getInstance(course_activity.this);
        Stetho.initializeWithDefaults(this);
        mListView = (ListView) findViewById(R.id.listView2);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(),attendance_activity.class);
                intent.putExtra("course",selectedItem);
                startActivity(intent);
            }
        });
        displayCourses();
        View fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(course_activity.this);
                View promptsView = li.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        course_activity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText name = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                final EditText course_code = (EditText) promptsView.findViewById(R.id.coursecode);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
//                                        result.setText(userInput.getText());
                                        String temp = name.getText().toString();
                                        String temp2 = course_code.getText().toString();
                                        Log.d(TAG,temp);
                                        new InsertTask(course_activity.this,temp,temp2).execute();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }
    private static class InsertTask extends AsyncTask<Void,Void,Boolean> {
        private String course_code;
        private String name;
        private Course course;


        private WeakReference<course_activity> activityReference;

        // only retain a weak reference to the activity
        InsertTask(course_activity context, String name,String course_code) {
            activityReference = new WeakReference<>(context);
            this.course_code=course_code;
            this.name=name;
            this.course = new Course(course_code,name);
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            if(activityReference!= null) {
                activityReference.get().appDatabase.courseDao().insert(this.course);

            }
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool){
                activityReference.get().setResult();
            }
        }

    }
    private void setResult(){
        displayCourses();
//        finish();
    }
    private void displayCourses(){
        appdb = AppDatabase.getInstance(course_activity.this);
        new RetrieveTask(this).execute();
    }
    private static class RetrieveTask extends AsyncTask<Void,Void, List<String>>{
        private WeakReference<course_activity> activityReference;

        RetrieveTask(course_activity context){
            activityReference=new WeakReference<>(context);
        }
        @Override
        protected List<String> doInBackground(Void... voids){
            if (activityReference.get() != null)
                return activityReference.get().appdb.courseDao().getnames();
            else
                return null;
        }
        @Override
        protected void onPostExecute(List<String> courses){
//            if(courses!= null && courses.size() >0) {
                activityReference.get().courses = courses;
                if(courses !=null&&courses.size()>0){
                    String[] sbev = new String[courses.size()];
                    sbev = courses.toArray(sbev);
                    activityReference.get().adapter = new ArrayAdapter<String>(activityReference.get(), R.layout.simple_list_item_1, sbev);
                    activityReference.get().mListView.setAdapter(activityReference.get().adapter);}
                else{
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add("No Courses");
                    activityReference.get().adapter = new ArrayAdapter<String>(activityReference.get(), R.layout.simple_list_item_1, temp);
                    activityReference.get().mListView.setAdapter(activityReference.get().adapter);}
//            }
        }
    }

}
