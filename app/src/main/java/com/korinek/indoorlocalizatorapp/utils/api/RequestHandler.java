package com.korinek.indoorlocalizatorapp.utils.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.mapper.RoomMapper;
import com.korinek.indoorlocalizatorapp.model.BuildingApiModel;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.model.RoomApiModel;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHandler implements ApiCalls{

    Context context;
    ApiService apiService;

    public RequestHandler(Context context) {
        this.context = context;
        apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public interface BuildingDataCallback {
        void onSuccess(BuildingApiModel data);
        void onFailure(String errorMessage);
    }

    public void getData(BuildingDataCallback callback) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String apiUrl = sharedPreferences.getString("settings_teco_api_url", "");

        Call<BuildingApiModel> call = apiService.getBuildingData(apiUrl);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BuildingApiModel> call, @NonNull Response<BuildingApiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e("RequestHandler", "Error loading data: " + response.message());
                    callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_data_load), response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BuildingApiModel> call, @NonNull Throwable t) {
                Log.e("RequestHandler", "Network error: " + t.getMessage());
                callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_network), t.getMessage()));
            }
        });
    }

    @Override
    public void getRoomsFromApi(RoomsFromApiCallback callback) {
        getData(new BuildingDataCallback() {
            @Override
            public void onSuccess(BuildingApiModel data) {
                List<Room> rooms = getRoomsFromBuildingApi(data);
                callback.onSuccess(rooms);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("RequestHandler", errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }

    private List<Room> getRoomsFromBuildingApi(BuildingApiModel data) {
        SharedPreferencesHelper sharedPrefer = new SharedPreferencesHelper(context);
        int buildingId = sharedPrefer.getBuilding().getId();
        List<RoomApiModel> roomsApiModel = data.getRooms();
        return RoomMapper.toRoomList(roomsApiModel, buildingId);
    }

    @Override
    public void getRoomData(String name, RoomDataCallback callback) {
        getData(new BuildingDataCallback() {
            @Override
            public void onSuccess(BuildingApiModel data) {
                List<RoomApiModel> rooms = data.getRooms();
                RoomApiModel room = getRoomFromBuildingApiRooms(rooms, name);
                if (room != null) {
                    callback.onSuccess(room);
                } else {
                    Log.e("RequestHandler", "Error: room not found in TecoApi");
                    callback.onFailure(context.getString(R.string.request_error_room_not_in_teco_api));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("RequestHandler", errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }

    private RoomApiModel getRoomFromBuildingApiRooms(List<RoomApiModel> rooms, String name) {
        return rooms.stream()
                .filter(room -> room.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
