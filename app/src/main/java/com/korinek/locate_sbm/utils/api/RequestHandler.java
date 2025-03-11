package com.korinek.locate_sbm.utils.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.mapper.RoomMapper;
import com.korinek.locate_sbm.model.api.BuildingApiModel;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.model.api.RoomApiModel;
import com.korinek.locate_sbm.model.api.ApiErrorModel;
import com.korinek.locate_sbm.utils.SharedPreferencesHelper;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHandler implements ApiCalls{

    private final String GET_ENDPOINT = "/GetObject";
    private final String SET_ENDPOINT = "/SetObject";
    Context context;
    ApiService apiService;

    /*
    URL adresy:
    - https://147.230.77.197/TecoApi/GetObject?_byt
    - https://147.230.77.197/TecoApi/GetObject?_dum

    GET - https://147.230.77.197/TecoApi/GetObject?_dum
    SET - https://147.230.77.197/TecoApi/SetObject?_dum[1].valve=67

    jmeno: test
    heslo: test

    Pokud je hodnota záporná, berte to jako "není definováno",
    rezerva pro obecnější použití (třeba 2. nebo 3, světlo v místnosti, snímač CO2, žaluzie ap.)
    a ve vizualizaci by se nemělo objevit.

    */

    public RequestHandler(Context context) {
        this.context = context;
        apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public interface BuildingDataCallback {
        void onSuccess(BuildingApiModel data);
        void onFailure(String errorMessage);
    }

    public void getData(BuildingDataCallback callback) {
        String url;
        try {
            url = getUrl(GET_ENDPOINT);
        } catch (IllegalArgumentException e) {
            callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_data_load), e.getMessage()));
            return;
        }

        Call<BuildingApiModel> call = apiService.getBuildingData(url);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BuildingApiModel> call, @NonNull Response<BuildingApiModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    Log.e("RequestHandler", "Error loading data: " + response.message());
                    String errorMessage = String.valueOf(response.code());
                    if(!response.message().isEmpty()) {
                        errorMessage = errorMessage + " - " + response.message();
                    }
                    callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_data_load), errorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<BuildingApiModel> call, @NonNull Throwable t) {
                Log.e("RequestHandler", "Network error: " + t.getMessage());
                callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_network), t.getMessage()));
            }
        });
    }

    private String getUrl(String endpoint) throws IllegalArgumentException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String apiUrl = sharedPreferences.getString("settings_teco_api_url", "");
        String apiBuildingName = sharedPreferences.getString("settings_teco_api_building_name", "");

        // remove slash at the end to prevent duplicate
        if (apiUrl.endsWith("/")) {
            apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
        }
        checkUrlValidity(apiUrl);
        return apiUrl + endpoint + "?" + apiBuildingName;
    }

    private void checkUrlValidity(String url) throws IllegalArgumentException {
        String urlRegex = "^(https?:\\/\\/)(([\\w.-]+)\\.([a-z]{2,6})|((\\d{1,3}\\.){3}\\d{1,3}))([\\/\\w .-]*)*\\/?$";
        if (!url.matches(urlRegex)) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(), context.getString(R.string.request_error_invalid_url), url));
        }
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

    @Override
    public void setRoomAttribute(String roomName, String attribute, Object newValue, SetRoomAttributeCallback callback) {
        getData(new BuildingDataCallback() {
            @Override
            public void onSuccess(BuildingApiModel data) {
                List<RoomApiModel> rooms = data.getRooms();
                int roomIndex = IntStream.range(0, rooms.size())
                    .filter(i -> rooms.get(i).getName().equalsIgnoreCase(roomName))
                    .findFirst()
                    .orElse(-1);

                if(roomIndex == -1) {
                    Log.e("RequestHandler", "Error: room that is going to be edited not found in TecoApi");
                    callback.onFailure(context.getString(R.string.request_error_edited_room_not_in_teco_api));
                } else {
                    // increase by one because PLC index starts from 1
                    roomIndex++;
                }

                String url;
                try {
                    url = getUrl(SET_ENDPOINT);
                } catch (IllegalArgumentException e) {
                    callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_data_load), e.getMessage()));
                    return;
                }

                String setUrl = url + "[" + roomIndex + "]." + attribute + "=" + newValue;
                Log.d("RequestHandler", "Url to set attribute: " + setUrl);
                setAttribute(setUrl, callback);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("RequestHandler", errorMessage);
                callback.onFailure(errorMessage);
            }
        });
    }

    private void setAttribute(String setUrl, SetRoomAttributeCallback callback) {
        Call<ResponseBody> call = apiService.setRoomData(setUrl);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    try {
                        String errorMessage = getErrorMessageFromErrorResponse(response);

                        if(errorMessage != null) {
                            Log.e("API", "Server error: " + errorMessage);
                            callback.onFailure("Chyba serveru: " + errorMessage);
                        } else {
                            Log.e("API", "Error while reading error response!");
                            callback.onFailure(context.getString(R.string.request_error_reading_error_response));
                        }
                    } catch (Exception e) {
                        Log.e("API", "Error while reading error response: " + e.getMessage());
                        callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_reading_error_response_message), e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("RequestHandler", "Network error: " + t.getMessage());
                callback.onFailure(String.format(Locale.getDefault(), context.getString(R.string.request_error_network), t.getMessage()));
            }
        });
    }

    private String getErrorMessageFromErrorResponse(Response<ResponseBody> response) {
        try {
            Gson gson = new Gson();
            ApiErrorModel apiError = gson.fromJson(response.errorBody().charStream(), ApiErrorModel.class);
            return (apiError != null && apiError.getError() != null) ? apiError.getError().getMessage() : context.getString(R.string.request_error_unknown);
        } catch (Exception e) {
            return null;
        }
    }
}
