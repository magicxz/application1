package com.example.application1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DaoAccess {

    @Query("SELECT * FROM contacts")
    List<Contact> getAllContact();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContact(Contact... contacts);

    @Update
    void updateContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);
}
