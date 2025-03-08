package com.korinek.indoorlocalizatorapp.ui.room_setup;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.indoorlocalizatorapp.model.api.RoomApiModel;
import com.korinek.indoorlocalizatorapp.utils.api.ApiCalls;
import com.korinek.indoorlocalizatorapp.utils.api.RequestHandler;

public class RoomSetupViewModel extends AndroidViewModel {

    private final MutableLiveData<String> roomName;
    private final MutableLiveData<RoomApiModel> room;
    private final MutableLiveData<String> errorText;


    public RoomSetupViewModel(Application application) {
        super(application);
        roomName = new MutableLiveData<>();
        room = new MutableLiveData<>();
        errorText = new MutableLiveData<>();
        loadData();
    }

    public void loadData() {
        RequestHandler requestHandler = new RequestHandler(getApplication());

        if(roomName != null && roomName.getValue() != null) {
            requestHandler.getRoomData(roomName.getValue(), new ApiCalls.RoomDataCallback() {
                @Override
                public void onSuccess(RoomApiModel roomApiModel) {
                    room.setValue(roomApiModel);
                    errorText.setValue(null);
                }

                @Override
                public void onFailure(String errorMessage) {
                    room.setValue(null);
                    errorText.setValue(errorMessage);
                }
            });
        }
    }

    public void setRoomName(String name) {
        roomName.setValue(name);
        loadData();
    }

    public LiveData<String> getRoomName() {
        return roomName;
    }

    public MutableLiveData<RoomApiModel> getRoom() {
        return room;
    }

    public LiveData<String> getErrorText() {
        return errorText;
    }
}
