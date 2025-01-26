package com.korinek.indoorlocalizatorapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.korinek.indoorlocalizatorapp.model.Building;

import java.util.List;

@Dao
public interface BuildingDao {
    @Insert
    void insert(Building building);

    @Update
    void update(Building building);

    @Delete
    void delete(Building building);

    @Query("SELECT * FROM buildings")
    LiveData<List<Building>> getAllBuildings();

    @Query("SELECT * FROM buildings WHERE id = :id LIMIT 1")
    Building getBuildingById(int id);

    @Query("DELETE FROM buildings")
    void deleteAllBuildings();
}
