package com.polinakrukovich.android.pizzaalmanac;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.FragmentSettingsBinding;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;

public class SettingsFragment extends DialogFragment implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        SettingsObserver {

    public static final String TAG = "SettingsFragment";

    private FragmentSettingsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);

        mBinding.settingsButtonBack.setOnClickListener(this);
        mBinding.red.setOnClickListener(this);
        mBinding.purple.setOnClickListener(this);
        mBinding.blue.setOnClickListener(this);
        mBinding.green.setOnClickListener(this);
        mBinding.yellow.setOnClickListener(this);
        mBinding.orange.setOnClickListener(this);
        mBinding.grey.setOnClickListener(this);

        mBinding.spinnerFontSize.setOnItemSelectedListener(this);
        mBinding.spinnerFontFamily.setOnItemSelectedListener(this);

        AppConfig.getInstance().addObserver(this);
        updateBackground(AppConfig.getInstance().getBackgroundColorId());
        updateFontFamily(AppConfig.getInstance().getFontFamily());
        updateFontSize(AppConfig.getInstance().getFontSize());
        mBinding.spinnerFontFamily.setSelection(AppConfig.getInstance().getFontFamilyPosition());
        mBinding.spinnerFontSize.setSelection(AppConfig.getInstance().getFontSizePosition());

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppConfig.getInstance().removeObserver(this);
        mBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void onButtonBackClicked() {
        dismiss();
    }


    @Override
    public void onClick(View v) {
        int colorId = 0;
        switch (v.getId()) {
            case R.id.settingsButtonBack:
                onButtonBackClicked();
                break;
            case R.id.red:
                colorId = R.color.colorBackgroundRed;
                break;
            case R.id.purple:
                colorId = R.color.colorBackgroundPurple;
                break;
            case R.id.blue:
                colorId = R.color.colorBackgroundBlue;
                break;
            case R.id.green:
                colorId = R.color.colorBackgroundGreen;
                break;
            case R.id.yellow:
                colorId = R.color.colorBackgroundYellow;
                break;
            case R.id.orange:
                colorId = R.color.colorBackgroundOrange;
                break;
            case R.id.grey:
                colorId = R.color.colorBackgroundGrey;
                break;
        }
        if (colorId != 0) {
            AppConfig.getInstance().setBackgroundColorId(colorId);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerFontSize:
                AppConfig.getInstance().setFontSize(position);
                break;
            case R.id.spinnerFontFamily:
                AppConfig.getInstance().setFontFamily(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void updateBackground(int id) {
        mBinding.settingsDialog.setBackgroundColor(getResources().getColor(id, null));
    }

    @Override
    public void updateFontSize(int size) {
        mBinding.exampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void updateFontFamily(String family) {
        mBinding.exampleText.setTypeface(Typeface.create(family, Typeface.NORMAL));
    }
}
