package com.example.application1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Contact.class}, version = 1)
//@TypeConverters({Contact.Converters.class})
public abstract class MyDatabase extends RoomDatabase {

    public abstract DaoAccess daoAccess();

    private static MyDatabase INSTANCE;

    public static MyDatabase getDBInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class,"Contact_DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
