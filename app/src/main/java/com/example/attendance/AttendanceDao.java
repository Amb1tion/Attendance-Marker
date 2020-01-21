package com.example.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE course LIKE :course AND date LIKE :date AND section LIKE :section")
    List<Attendance> attendance_instance(String course, Date date,String section);
    @Insert
    void insert(Attendance attendance);
}
