package com.example.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


public class Activity2 extends Activity {

    Pattern  rollno_match = Pattern.compile("^\\w\\w-\\d+");
    Pattern section_match = Pattern.compile("Section-\\w");
    String rollno;
    String name;
    String section;
    Boolean grab_name=false;
    Boolean add=false;
    private Student student;
    private AppDatabase appDatabase;
    ArrayList<String> sections;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.attendance";
//    final List<Student> studentList = new List<>(); //this is a new student list object , figure out how to load an old stored one


    @Override
    public void onCreate(Bundle Icicle){
        super.onCreate(Icicle);
        setContentView(R.layout.activity2);

        appDatabase= AppDatabase.getInstance(Activity2.this);
        Stetho.initializeWithDefaults(this);
        sections = new ArrayList<>();
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                startActivityForResult(intent, 7);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch(requestCode){

            case 7:

                if(resultCode==RESULT_OK)
                {
                    Uri uri = data.getData();
                    InputStream stream = null;
                    try {
                        stream = getContentResolver().openInputStream(uri);
                        XSSFWorkbook workbook = new XSSFWorkbook(stream);
                        XSSFSheet sheet = workbook.getSheetAt(0);
                        int lastrow= getLastRowWithData(sheet);
//                        int rowsCount = sheet.getPhysicalNumberOfRows();
                        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        for (int r = 0; r<lastrow; r++) {
                            Row row = sheet.getRow(r);
                            int cellsCount = row.getPhysicalNumberOfCells();
                            for (int c = 0; c<cellsCount; c++) {
                                String value = getCellAsString(row, c, formulaEvaluator);
                                if (grab_name){ //2nd step of logic , if name is true we add the rollno and name to studentlist from the next column
                                    name=value;
                                    grab_name=false;
                                    add = true;
                                }
                                else if(rollno_match.matcher(value).matches()){ //logic starts here , if rollno is found we grab name in next iteration
                                    rollno = value;
                                    grab_name=true;
                                }
                                else if(section_match.matcher(value).matches()&& section != value){
                                    section = value;
                                    sections.add(value);
                                }
                            }
                            if (add){
                                Student temp= new Student(name,rollno,"Present",section);

                                Log.d("stuff",temp.toString());
                                add=false;

                                new InsertTask(Activity2.this,temp).execute();}


                        }
                    }
                    catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    catch (IOException e) {
                            e.printStackTrace();
                        }

                    mPreferences = Activity2.this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPreferences.edit();
                    try {
                        editor.putString("Sections",ObjectSerializer.serialize(sections));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editor.commit();

                    Intent intent = new Intent(this, course_activity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList("mylist", studentList);
//                    intent.putExtras(bundle);
                    this.startActivity(intent);


                }
                break;
        }
    }
    protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
//            printlnToUser(e.toString());
        }
        return value;
    }
    private static class InsertTask extends AsyncTask<Void,Void,Boolean> {

        private Student student;
        private WeakReference<Activity2> activityReference;

        // only retain a weak reference to the activity
        InsertTask(Activity2 context, Student student) {
            activityReference = new WeakReference<>(context);
            this.student = student;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs) {
            if(activityReference!= null) {
                activityReference.get().appDatabase.studentDao().insert(student);

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
    public int getLastRowWithData(XSSFSheet sheet) {
        int rowCount = 0;
        Iterator<Row> iter = sheet.rowIterator();

        while (iter.hasNext()) {
            Row r = iter.next();
            if (!this.isRowBlank(r)) {
                rowCount = r.getRowNum();
            }
        }

        return rowCount;
    }

    public boolean isRowBlank(Row r) {
        boolean ret = true;

        /*
         * If a row is null, it must be blank.
         */
        if (r != null) {
            Iterator<Cell> cellIter = r.cellIterator();
            /*
             * Iterate through all cells in a row.
             */
            while (cellIter.hasNext()) {
                /*
                 * If one of the cells in given row contains data, the row is
                 * considered not blank.
                 */
                if (!this.isCellBlank(cellIter.next())) {
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }

    public boolean isCellBlank(Cell c) {
        return (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK);
    }

    public boolean isCellEmpty(Cell c) {
        return (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK || (c
                .getCellType() == Cell.CELL_TYPE_STRING && c
                .getStringCellValue().isEmpty()));
    }
    private void setResult(){
        finish();
    }
}
