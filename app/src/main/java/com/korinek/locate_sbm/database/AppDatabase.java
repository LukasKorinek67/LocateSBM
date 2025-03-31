package com.korinek.locate_sbm.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.dao.BuildingDao;
import com.korinek.locate_sbm.dao.RoomDao;
import com.korinek.locate_sbm.model.Building;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.WifiFingerprint;

@Database(entities = {Building.class, Room.class, WifiFingerprint.class}, version = 1)
@TypeConverters({Converters.class})
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
                            context.getString(R.string.database_name)
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
