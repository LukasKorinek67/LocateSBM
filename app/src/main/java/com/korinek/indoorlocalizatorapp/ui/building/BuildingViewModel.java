package com.korinek.indoorlocalizatorapp.ui.building;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.indoorlocalizatorapp.database.AppDatabase;
import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;
import com.korinek.indoorlocalizatorapp.utils.api.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        changeTecoApiIntegrationInPreferences(building);

        //reset API client - new URL and authorization
        ApiClient.resetClient();
    }

    public void setBuildingTecoApiUrl(String url) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingTecoUrl(building.getId(), url));
    }

    public void setUseAuthorization(boolean useAuth) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingUseAuthorization(building.getId(), useAuth));
    }

    public void setAuthorizationUsername(String username) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingAuthUsername(building.getId(), username));
    }

    public void setAuthorizationPassword(String password) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingAuthPassword(building.getId(), password));
    }

    private void changeTecoApiIntegrationInPreferences(Building building) {
        SharedPreferences sharedPreferences = getApplication()
                .getSharedPreferences("com.korinek.indoorlocalizatorapp_preferences", Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString("settings_teco_api_url", building.getIntegration().getTecoApiUrl())
                .putBoolean("settings_request_authorization", building.getIntegration().isUseAuthorization())
                .putString("settings_request_authorization_username", building.getIntegration().getUsername())
                .putString("settings_request_authorization_password", building.getIntegration().getPassword())
                .apply();
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
        executorService.execute(() -> {
            try {
                database.roomDao().insert(room);
                List<Room> roomsList = rooms.getValue();
                if (roomsList != null) {
                    roomsList.add(room);
                    rooms.postValue(roomsList);
                }
            } catch (SQLiteConstraintException e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication().getApplicationContext(),
                                "Nelze přidat místnost, název je již použit v této budově.",
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void updateRoom(Room room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                database.roomDao().update(room);
                List<Room> roomsList = rooms.getValue();
                if (roomsList != null) {
                    roomsList = roomsList.stream()
                            .map(orginalRoom -> orginalRoom.getName().equals(room.getName()) ? room : orginalRoom)
                            .collect(Collectors.toList());
                    rooms.postValue(roomsList);
                }
            } catch (SQLiteConstraintException e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication().getApplicationContext(),
                                "Nelze upravit místnost! Problém v databázi",
                                Toast.LENGTH_LONG).show()
                );
            }
        });
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