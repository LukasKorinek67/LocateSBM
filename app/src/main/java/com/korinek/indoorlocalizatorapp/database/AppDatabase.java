package com.korinek.indoorlocalizatorapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.korinek.indoorlocalizatorapp.dao.BuildingDao;
import com.korinek.indoorlocalizatorapp.dao.RoomDao;
import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.model.Room;

@Database(entities = {Building.class, Room.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    public abstract BuildingDao buildingDao();
    public abstract RoomDao roomDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = androidx.room.Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
