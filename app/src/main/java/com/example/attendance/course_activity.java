package com.example.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class course_activity extends AppCompatActivity {
    private static final String TAG = "sbev";
    private AppDatabase appdb;
    private ArrayAdapter<String> adapter;
    private ListView mListView;
    private List<String> courses;
    private FloatingActionButton fab;
    private String course_code;
    private String name;
    private AppDatabase appDatabase;
    private List<Attendance> att_list;
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
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                LayoutInflater li = LayoutInflater.from(course_activity.this);
                View promptsView = li.inflate(R.layout.inprogress,null);
                AlertDialog alertDialogBuilder = new AlertDialog.Builder(course_activity.this).create();
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.show();
                //code to get all attendance instances from attendance table
                new ExportTask(course_activity.this,selectedItem,alertDialogBuilder).execute();


                return true;
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
    private static class ExportTask extends AsyncTask<Void,Void,List<Attendance>>{
        private WeakReference<course_activity> activityReference;
        private String course;
        private AlertDialog alertDialog;
        private List<Attendance> A_Attendances; //TODO MAKE THESE GENERATE FROM SHAREDPREFERENCES
        private List<Attendance> B_Attendances;
        private List<Attendance> C_Attendances;
        private List<Attendance> D_Attendances;
        private List<Attendance> E_Attendances;
        private List<Attendance> F_Attendances;

        ExportTask(course_activity context,String course,AlertDialog alertDialog){
            this.alertDialog=alertDialog;
            this.course=course;
            activityReference=new WeakReference<>(context);}
        @Override
        protected List<Attendance> doInBackground(Void... voids){
            if(activityReference.get() != null)
            {//TODO MAKE THIS NOT INCREDIBLY JANKY , SECTION STRING COULD CHANGE DEPENDING ON EXCEL IMPORT ???
                this.A_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-A");
                this.B_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-B");
                this.C_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-C");
                this.D_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-D");
                this.E_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-E");
                this.F_Attendances=activityReference.get().appDatabase.attendanceDao().section_attendance(this.course,"Section-F");
                Log.d(TAG,this.B_Attendances.toString());
                return activityReference.get().appDatabase.attendanceDao().attendance_instance(this.course);

            }
            else
                return null;
        }
        @Override
        protected void onPostExecute(List<Attendance> attendances){
            activityReference.get().att_list = attendances;
            // Blank workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            activityReference.get().putSheet(this.A_Attendances,workbook,"Section-A");
            activityReference.get().putSheet(this.B_Attendances,workbook,"Section-B");
            activityReference.get().putSheet(this.C_Attendances,workbook,"Section-C");
            activityReference.get().putSheet(this.D_Attendances,workbook,"Section-D");
            activityReference.get().putSheet(this.E_Attendances,workbook,"Section-E");
            activityReference.get().putSheet(this.F_Attendances,workbook,"Section-F");

            String filename= this.course+".xlsx";
            String extstorage = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extstorage,"Attendance");
            folder.mkdirs();
            File file = new File(folder,filename);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                workbook.write(fileOut);
                fileOut.close();

                Toast toast = Toast.makeText(activityReference.get(),"Excel File Saved to App Folder",Toast.LENGTH_SHORT);
                toast.show();
            } catch (FileNotFoundException e) {
                Toast toast = Toast.makeText(activityReference.get(),"FIle could not save",Toast.LENGTH_SHORT);
                toast.show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(activityReference.get(),"FIle could not save",Toast.LENGTH_SHORT);
                toast.show();
            }
            this.alertDialog.dismiss();

        }

}
    private void putSheet(List<Attendance> attendances,XSSFWorkbook workbook,String section){
        if(attendances.size() !=0 ){
            XSSFSheet sheet = workbook.createSheet(section);
            Row row= sheet.createRow(0);
            row.createCell(0).setCellValue("Name");
            row.createCell(1).setCellValue("Roll");
            for (int i=0; i<attendances.size();i++){
                Attendance temp=attendances.get(i);
                Date date = temp.getDate();
                SimpleDateFormat ft = new SimpleDateFormat("dd.MM");
                String interm = ft.format(date);
//                String out = interm + " Slots= "+ temp.getLectures();
                row.createCell(i+2).setCellValue(interm);
            }
            for(int k = 0; k<attendances.size();k++){
                Attendance temp = attendances.get(k);
                List<Student> temp_list= temp.getStudentlist();
                for(int j = 0; j <temp_list.size();j++){

                    if(k == 0){
                        Row temp_row= sheet.createRow(j+1);
                        temp_row.createCell(0).setCellValue(temp_list.get(j).getName());
                        temp_row.createCell(1).setCellValue(temp_list.get(j).getRollno());
                        temp_row.createCell(2).setCellValue(temp_list.get(j).getAttendance());
                    }
                    else{
                        Row temp_row= sheet.getRow(j+1);
                        temp_row.createCell(k+2).setCellValue(temp_list.get(j).getAttendance());
                    }
                }
            }
        }
    }



}
