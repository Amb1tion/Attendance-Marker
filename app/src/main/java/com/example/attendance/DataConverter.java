package com.example.attendance;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Date;

import androidx.room.TypeConverter;

public class DataConverter implements Serializable {
    @TypeConverter
    public String fromStudentList(List<Student> StudentList) {
        if (StudentList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Student>>() {
        }.getType();
        String json = gson.toJson(StudentList, type);
        return json;
    }
    @TypeConverter
    public List<Student> toStudentList(String StudentListString) {
        if (StudentListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Student>>() {
        }.getType();
        List<Student> productCategoriesList = gson.fromJson(StudentListString, type);
        return productCategoriesList;
    }
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
