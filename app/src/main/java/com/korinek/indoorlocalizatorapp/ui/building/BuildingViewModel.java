package com.korinek.indoorlocalizatorapp.ui.building;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.indoorlocalizatorapp.database.AppDatabase;
import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingViewModel extends AndroidViewModel {
    private final AppDatabase database;
    SharedPreferencesHelper sharedPreferencesHelper;
    private final MutableLiveData<Boolean> isBuildingSelected;
    private final MutableLiveData<List<Room>> rooms;

    public BuildingViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        sharedPreferencesHelper = new SharedPreferencesHelper(application.getApplicationContext());
        isBuildingSelected = new MutableLiveData<>();
        isBuildingSelected.setValue(sharedPreferencesHelper.isBuildingSelected());
        rooms = new MutableLiveData<>();
        if(sharedPreferencesHelper.isBuildingSelected()) {
            setRooms(sharedPreferencesHelper.getBuilding());
        } else {
            List<Room> roomsList = new ArrayList<>();
            rooms.setValue(roomsList);
        }
    }

    public LiveData<List<Building>> getAllBuildings() {
        return database.buildingDao().getAllBuildings();
    }

    public void insertBuilding(Building building) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().insert(building));
    }

    public void deleteBuilding(Building building) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().delete(building));
    }

    public MutableLiveData<Boolean> getIsBuildingSelected() {
        return isBuildingSelected;
    }

    public void setBuilding(Building building) {
        sharedPreferencesHelper.saveBuilding(building);
        sharedPreferencesHelper.saveTheme(building.getColor());
        isBuildingSelected.setValue(true);

        //set rooms for building
        setRooms(building);
    }

    private void setRooms(Building building) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<Room> roomsList = database.roomDao().getRoomsForBuilding(building.getId());
            if (roomsList != null) {
                rooms.postValue(roomsList);
            }
        });
    }

    public Building getSelectedBuilding() {
        return sharedPreferencesHelper.getBuilding();
    }

    public void unselectBuilding() {
        sharedPreferencesHelper.removeBuilding();
    }

    public LiveData<List<Room>> getRooms() {
        return rooms;
    }

    public void insertRoom(Room room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.roomDao().insert(room));

        List<Room> roomsList = rooms.getValue();
        if(roomsList != null && room != null) {
            roomsList.add(room);
            rooms.setValue(roomsList);
        }
    }

    public void deleteRoom(Room room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.roomDao().delete(room));

        List<Room> roomsList = rooms.getValue();
        if(roomsList != null && room != null) {
            roomsList.remove(room);
            rooms.setValue(roomsList);
        }
    }
}