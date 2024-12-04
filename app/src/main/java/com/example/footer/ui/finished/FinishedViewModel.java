package com.example.footer.ui.finished;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinishedViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public FinishedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is finished fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}