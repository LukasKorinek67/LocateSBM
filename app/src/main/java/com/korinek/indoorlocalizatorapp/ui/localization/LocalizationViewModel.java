package com.korinek.indoorlocalizatorapp.ui.localization;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocalizationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LocalizationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is localization fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}