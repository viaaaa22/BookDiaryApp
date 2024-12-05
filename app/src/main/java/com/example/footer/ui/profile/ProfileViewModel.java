package com.example.footer.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> username;

    public ProfileViewModel() {
        username = new MutableLiveData<>();
    }

    public void setUsername(String name) {
        username.setValue(name);
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public void logout() {
        username.setValue("");
    }
}