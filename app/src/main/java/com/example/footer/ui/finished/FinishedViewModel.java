package com.example.footer.ui.finished;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FinishedViewModel extends ViewModel {
    private final MutableLiveData<Integer> finishedCount;
    private final MutableLiveData<Integer> wishlistCount;

    public FinishedViewModel() {
        finishedCount = new MutableLiveData<>();
        wishlistCount = new MutableLiveData<>();
    }

    public void setFinishedCount(int count) {
        finishedCount.setValue(count);
    }

    public void setWishlistCount(int count) {
        wishlistCount.setValue(count);
    }

    public LiveData<Integer> getFinishedCount() {
        return finishedCount;
    }

    public LiveData<Integer> getWishlistCount() {
        return wishlistCount;
    }
}