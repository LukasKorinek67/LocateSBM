package com.korinek.indoorlocalizatorapp.ui.building;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BuildingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BuildingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is building fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}