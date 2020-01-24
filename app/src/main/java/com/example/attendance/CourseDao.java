package com.example.attendance;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CourseDao {
    @Query("SELECT name FROM course")
    List<String> getnames();
    @Insert
    void insert(Course course);
    @Query("DELETE FROM course WHERE name LIKE :name")
    void del_course(String name);
}
