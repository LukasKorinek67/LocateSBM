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

    @Query("UPDATE buildings SET color = :color WHERE id = :buildingId")
    void updateBuildingColor(int buildingId, int color);

    @Query("UPDATE buildings SET tecoApiUrl = :tecoApiUrl WHERE id = :buildingId")
    void updateBuildingTecoUrl(int buildingId, String tecoApiUrl);

    @Query("UPDATE buildings SET tecoApiBuildingName = :tecoApiName WHERE id = :buildingId")
    void updateBuildingTecoName(int buildingId, String tecoApiName);

    @Query("UPDATE buildings SET useAuthorization = :useAuth WHERE id = :buildingId")
    void updateBuildingUseAuthorization(int buildingId, boolean useAuth);

    @Query("UPDATE buildings SET username = :username WHERE id = :buildingId")
    void updateBuildingAuthUsername(int buildingId, String username);

    @Query("UPDATE buildings SET password = :password WHERE id = :buildingId")
    void updateBuildingAuthPassword(int buildingId, String password);
}
