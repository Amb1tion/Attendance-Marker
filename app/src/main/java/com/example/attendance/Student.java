package com.example.attendance;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName="student")
public class Student implements Parcelable {
//    @PrimaryKey(autoGenerate=true)
//    private int uid;
    @ColumnInfo(name="name")
    private String name;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="roll_no")
    private String rollno;
    private String attendance;
    @ColumnInfo(name="section")
    private String section;

    public void setName(String name) {
        this.name = name;
    }

//    public int getUid() {
//        return uid;
//    }
//
//    public void setUid(int uid) {
//        this.uid = uid;
//    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public String getRollno() {
        return rollno;
    }

    public String getAttendance() {
        return attendance;
    }

    public String getSection() {
        return section;
    }

    public Student(String name, String rollno, String attendance, String section) {
        this.name = name;
        this.rollno = rollno;
        this.attendance = attendance;
        this.section = section;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub
        dest.writeString(rollno);
        dest.writeString(name);
        dest.writeString(attendance);
        dest.writeString(section);
    }

    public Student(Parcel in) {
        rollno = in.readString();
        name = in.readString();
        attendance = in.readString();
        section= in.readString();
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        public Student[] newArray(int size) {
            return new Student[size];
        }
    };
}
