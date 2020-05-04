package com.polinakrukovich.android.pizzaalmanac.config;

import android.content.SharedPreferences;
import com.polinakrukovich.android.pizzaalmanac.R;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConfig {

    public enum Language { RU, EN }

    private static final class SingletonHolder {
        private final static AppConfig instance = new AppConfig();
    }

    private SharedPreferences sharedPreferences;
    private int backgroundColorId = R.color.colorBackgroundGrey;
    private Integer fontSizeKey = 1;
    private Integer fontFamilyKey = 0;
    private Language language = Language.EN;

    private List<SettingsObserver> observers = new ArrayList<>();

    private final Map<Integer, Integer> fontSizes = new HashMap<>();
    {
        fontSizes.put(0, 14);
        fontSizes.put(1, 16);
        fontSizes.put(2, 18);
    }

    private final Map<Integer, String> fontFamilies = new HashMap<>();
    {
        fontFamilies.put(0, "sans-serif");
        fontFamilies.put(1, "sans-serif-thin");
        fontFamilies.put(2, "sans-serif-light");
        fontFamilies.put(3, "sans-serif-medium");
        fontFamilies.put(4, "sans-serif-condensed");
    }

    private AppConfig() {}

    public static AppConfig getInstance() {
        return SingletonHolder.instance;
    }

    public void init(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        backgroundColorId = sharedPreferences.getInt("backgroundId", R.color.colorBackgroundGrey);
        fontSizeKey = sharedPreferences.getInt("fontSizePos", 1);
        fontFamilyKey = sharedPreferences.getInt("fontFamilyPos", 0);
    }

    private void saveState() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("backgroundId", backgroundColorId);
        editor.putInt("fontSizePos", fontSizeKey);
        editor.putInt("fontFamilyPos", fontFamilyKey);
        editor.apply();
    }

    public void setBackgroundColorId(int id) {
        this.backgroundColorId = id;
        for (SettingsObserver observer : observers) {
            if (null != observer) {
                observer.updateBackground(backgroundColorId);
            }
        }
        saveState();
    }

    public int getBackgroundColorId() {
        return backgroundColorId;
    }

    public void setFontSize(int position) {
        this.fontSizeKey = position;
        for (SettingsObserver observer : observers) {
            if (null != observer) {
                observer.updateFontSize(fontSizes.get(fontSizeKey));
            }
        }
        saveState();
    }

    public int getFontSize() {
        return fontSizes.get(fontSizeKey);
    }

    public int getFontSizePosition() {
        return fontSizeKey;
    }


    public void setFontFamily(int position) {
        this.fontFamilyKey = position;
        for (SettingsObserver observer : observers) {
            if (null != observer) {
                observer.updateFontFamily(fontFamilies.get(fontFamilyKey));
            }
        }
        saveState();
    }

    public int getFontFamilyPosition() {
        return fontFamilyKey;
    }

    public String getFontFamily() {
        return fontFamilies.get(fontFamilyKey);
    }

    public void addObserver(SettingsObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SettingsObserver observer) {
        observers.remove(observer);
    }
}
