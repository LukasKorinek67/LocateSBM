package com.korinek.locate_sbm.ui.building;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.database.AppDatabase;
import com.korinek.locate_sbm.model.Building;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.utils.SharedPreferencesHelper;
import com.korinek.locate_sbm.utils.api.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BuildingViewModel extends AndroidViewModel {
    private final AppDatabase database;
    SharedPreferencesHelper sharedPreferencesHelper;
    private final MutableLiveData<Boolean> isBuildingSelected;
    private final MutableLiveData<List<RoomWithWifiFingerprints>> rooms;

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
            List<RoomWithWifiFingerprints> roomsList = new ArrayList<>();
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

    public void setBuildingTecoApiName(String buildingTecoApiName) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingTecoName(building.getId(), buildingTecoApiName));
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
                .getSharedPreferences("com.korinek.locate_sbm_preferences", Context.MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putString("settings_teco_api_url", building.getIntegration().getTecoApiUrl())
                .putString("settings_teco_api_building_name", building.getIntegration().getTecoApiBuildingName())
                .putBoolean("settings_request_authorization", building.getIntegration().isUseAuthorization())
                .putString("settings_request_authorization_username", building.getIntegration().getUsername())
                .putString("settings_request_authorization_password", building.getIntegration().getPassword())
                .apply();
    }

    private void setRooms(Building building) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<RoomWithWifiFingerprints> roomsList = database.roomDao().getRoomsWithWifiFingerprintsForBuilding(building.getId());
            if (roomsList != null) {
                rooms.postValue(roomsList);
            }
        });
    }

    public void reloadRooms() {
        setRooms(sharedPreferencesHelper.getBuilding());
    }

    public Building getSelectedBuilding() {
        return sharedPreferencesHelper.getBuilding();
    }

    public void changeBuildingColor(int color) {
        Building building = getSelectedBuilding();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.buildingDao().updateBuildingColor(building.getId(), color));
        building.setColor(color);
        sharedPreferencesHelper.saveBuilding(building);
        sharedPreferencesHelper.saveTheme(color);
    }

    public void unselectBuilding() {
        isBuildingSelected.setValue(false);
        sharedPreferencesHelper.removeBuilding();
    }

    public LiveData<List<RoomWithWifiFingerprints>> getRooms() {
        return rooms;
    }

    public void insertRoom(RoomWithWifiFingerprints room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                database.roomDao().insertRoomWithWifiFingerprints(room);
                List<RoomWithWifiFingerprints> roomsList = rooms.getValue();
                if (roomsList != null) {
                    roomsList.add(room);
                    rooms.postValue(roomsList);
                }
            } catch (SQLiteConstraintException e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication().getApplicationContext(),
                                getApplication().getString(R.string.toast_add_room_error_duplicated_name),
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void updateRoom(RoomWithWifiFingerprints room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                database.roomDao().update(room.getRoom());
                List<RoomWithWifiFingerprints> roomsList = rooms.getValue();
                if (roomsList != null) {
                    roomsList = roomsList.stream()
                            .map(orginalRoom -> orginalRoom.getName().equals(room.getName()) ? room : orginalRoom)
                            .collect(Collectors.toList());
                    rooms.postValue(roomsList);
                }
            } catch (SQLiteConstraintException e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(getApplication().getApplicationContext(),
                                getApplication().getString(R.string.toast_edit_room_database_error),
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void deleteRoom(RoomWithWifiFingerprints room) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.roomDao().delete(room.getRoom()));

        List<RoomWithWifiFingerprints> roomsList = rooms.getValue();
        if(roomsList != null && room != null) {
            roomsList.remove(room);
            rooms.setValue(roomsList);
        }
    }
}