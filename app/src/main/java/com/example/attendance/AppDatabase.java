package com.example.attendance;

import android.content.Context;
import android.provider.SyncStateContract;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities={Student.class,Attendance.class,Course.class},version=1)
@TypeConverters({DataConverter.class})
public abstract class AppDatabase extends RoomDatabase{
    public abstract StudentDao studentDao();
    public abstract AttendanceDao attendanceDao();
    public abstract CourseDao courseDao();
    private static AppDatabase  appdb;
    public static AppDatabase getInstance(Context context){
        if(null == appdb){
            appdb=buildDatabaseInstance(context);
        }
        return appdb;
    }
    private static AppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "appdb.db")
                .allowMainThreadQueries().build();
    }
    public void cleanUp(){
        appdb = null;
    }

}
