package com.example.footer.ui.wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WishlistViewModel extends ViewModel {

    private final MutableLiveData<Integer> wishlistCount;

    public WishlistViewModel() {
        wishlistCount = new MutableLiveData<>();
    }

    public void setWishlistCount(int count) {
        wishlistCount.setValue(count);
    }

    public LiveData<Integer> getWishlistCount() {
        return wishlistCount;
    }
}