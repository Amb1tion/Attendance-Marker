package com.example.attendance;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAll();
    @Insert
    void insert(Student student);
    @Query("SELECT * FROM student WHERE section LIKE :section")
    List<Student> findBySection(String section);

}
