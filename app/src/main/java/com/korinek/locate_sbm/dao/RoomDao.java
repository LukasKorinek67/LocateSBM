package com.korinek.locate_sbm.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.korinek.locate_sbm.model.Room;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Room room);

    @Update(onConflict = OnConflictStrategy.ABORT)
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("SELECT * FROM rooms WHERE buildingId = :buildingId")
    List<Room> getRoomsForBuilding(int buildingId);
}
