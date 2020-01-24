package com.example.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE course LIKE :course ORDER BY date DESC")
    List<Attendance> attendance_instance(String course);
    @Insert
    void insert(Attendance attendance);
    @Query("SELECT * FROM attendance WHERE course LIKE :course AND section LIKE :section")
    List<Attendance> section_attendance(String course,String section);
}
