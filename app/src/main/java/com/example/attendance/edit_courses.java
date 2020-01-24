package com.example.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class edit_courses extends AppCompatActivity {
    private static final String TAG="edit_courses";
    private AppDatabase appDatabase;
    private List<String> courses;
    private ArrayAdapter adapter;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle SavedInstance){
        super.onCreate(SavedInstance);
        setContentView(R.layout.edit_courses);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appDatabase=AppDatabase.getInstance(edit_courses.this);
        mListView = (ListView) findViewById(R.id.listView2);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = (String) parent.getItemAtPosition(position);
                //execute thread to delete from db
                AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(edit_courses.this);
                alertDialogBuilder.setMessage(R.string.confirm);
                alertDialogBuilder.setTitle(R.string.warning);
                final AlertDialog alert_dialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTask(edit_courses.this,selectedItem).execute();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert_dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }

        });
        displayCourses();
    }
    private void displayCourses(){

        new edit_courses.RetrieveTask(this).execute();
    }
    private static class RetrieveTask extends AsyncTask<Void,Void, List<String>> {
        private WeakReference<edit_courses> activityReference;

        RetrieveTask(edit_courses context){
            activityReference=new WeakReference<>(context);
        }
        @Override
        protected List<String> doInBackground(Void... voids){
            if (activityReference.get() != null)
                return activityReference.get().appDatabase.courseDao().getnames();
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
    private static class DeleteTask extends AsyncTask<Void,Void, Boolean> {
        private WeakReference<edit_courses> activityReference;
        private String course;

        DeleteTask(edit_courses context,String course){
            activityReference=new WeakReference<>(context);
            this.course=course;
        }
        @Override
        protected Boolean doInBackground(Void... voids){
            if (activityReference.get() != null){
                activityReference.get().appDatabase.courseDao().del_course(course);
             }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){

            activityReference.get().displayCourses();

        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(getBaseContext(),course_activity.class);
        startActivity(intent);
        finish();
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getBaseContext(),course_activity.class);
        startActivity(intent);
        finish();
    }
}
