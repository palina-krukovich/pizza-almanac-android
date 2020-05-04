package com.polinakrukovich.android.pizzaalmanac.observer;

public interface SettingsObserver {
    void updateBackground(int id);
    void updateFontSize(int size);
    void updateFontFamily(String family);
}
