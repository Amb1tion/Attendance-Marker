package com.example.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName="attendance")
public class Attendance {
    @PrimaryKey
    @TypeConverters(DataConverter.class)
    public Date date;
    @ColumnInfo(name="course")
    private String course;
    @ColumnInfo(name="inst")
    @TypeConverters(DataConverter.class)
    @NonNull
    private List<Student> studentlist;
    @ColumnInfo(name="section")
    private String section;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Attendance(Date date) {
        this.date = date;
        this.course=course;
        this.studentlist=studentlist;
        this.section=section;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public List<Student> getStudentlist() {
        return studentlist;
    }

    public void setStudentlist(List<Student> studentlist) {
        this.studentlist = studentlist;
    }
}
