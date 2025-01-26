package com.korinek.indoorlocalizatorapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.korinek.indoorlocalizatorapp.model.Room;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert
    void insert(Room room);

    @Update
    void update(Room room);

    @Delete
    void delete(Room room);

    @Query("SELECT * FROM rooms WHERE buildingId = :buildingId")
    List<Room> getRoomsForBuilding(int buildingId);
}
