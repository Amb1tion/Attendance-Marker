package com.example.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE course LIKE :course")
    List<Attendance> attendance_instance(String course);
    @Insert
    void insert(Attendance attendance);
}
