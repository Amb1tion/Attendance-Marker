package com.example.attendance;


import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AppDatabase appdb;
    private List<Student> studentlist;
    private StudentListAdapter adapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"OnCreate: Started.");
//        appdb = AppDatabase.getInstance(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView);
        displayList();




    }
    private void displayList(){
// initialize database instance
        appdb = AppDatabase.getInstance(MainActivity.this);
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
                return activityReference.get().appdb.studentDao().getAll();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Student> studentlist) {
            if (studentlist!=null && studentlist.size()>0 ){
                activityReference.get().studentlist = studentlist;
                Log.d(TAG,studentlist.toString());

                // hides empty text view
//                activityReference.get().textViewMsg.setVisibility(View.GONE);

                // create and set the adapter on RecyclerView instance to display list
                activityReference.get().adapter = new StudentListAdapter(activityReference.get(),R.layout.adapter_view_layout,studentlist);
                activityReference.get().mListView.setAdapter(activityReference.get().adapter);

            }
        }


    }

    }
