package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName="course")
public class Course {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="course_code")
    private String course_code;
    @ColumnInfo(name="name")
    private String name;

    @NonNull
    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(@NonNull String course_code) {
        this.course_code = course_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course(@NonNull String course_code, String name) {
        this.course_code = course_code;
        this.name = name;
    }
}
