package com.polinakrukovich.android.pizzaalmanac.viewmodel;

import androidx.lifecycle.ViewModel;

import com.polinakrukovich.android.pizzaalmanac.util.FilterUtil;

public class MainActivityViewModel extends ViewModel {
    private boolean mIsSigningIn;
    private FilterUtil mFilterUtil;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mFilterUtil = FilterUtil.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public FilterUtil getFilterUtil() {
        return mFilterUtil;
    }

    public void setFilterUtil(FilterUtil mFilterUtil) {
        this.mFilterUtil = mFilterUtil;
    }
}
