package com.korinek.indoorlocalizatorapp.ui.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.korinek.indoorlocalizatorapp.database.AppDatabase;
import com.korinek.indoorlocalizatorapp.model.Building;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingViewModel2 extends AndroidViewModel {
    private final AppDatabase database;

    public BuildingViewModel2(Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Building>> getBuildings() {
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
}
