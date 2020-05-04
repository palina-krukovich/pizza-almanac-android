package com.polinakrukovich.android.pizzaalmanac;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.firestore.Query;
import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.FragmentFilterDialogBinding;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;
import com.polinakrukovich.android.pizzaalmanac.util.FilterUtil;

public class FilterDialogFragment extends DialogFragment implements
        View.OnClickListener,
        SettingsObserver {
    public static final String TAG = "FilterDialog";

    interface FilterListener {
        void onFilter(FilterUtil filters);
    }

    private FragmentFilterDialogBinding mBinding;
    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFilterDialogBinding.inflate(inflater, container, false);

        mBinding.buttonSearch.setOnClickListener(this);
        mBinding.buttonCancel.setOnClickListener(this);

        AppConfig.getInstance().addObserver(this);
        updateBackground(AppConfig.getInstance().getBackgroundColorId());

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppConfig.getInstance().removeObserver(this);
        mBinding = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilterUtil());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedOrigin() {
        String selected = (String) mBinding.spinnerOrigin.getSelectedItem();
        if (getString(R.string.any_origin).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Pizza.FIELD_RATING;
        } if (getString(R.string.sort_by_name).equals(selected)) {
            return Pizza.FIELD_NAME;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        } if (getString(R.string.sort_by_name).equals(selected)) {
            return Query.Direction.ASCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mBinding != null) {
            mBinding.spinnerOrigin.setSelection(0);
            mBinding.spinnerSort.setSelection(0);
        }
    }

    public FilterUtil getFilterUtil() {
        FilterUtil filterUtil = new FilterUtil();

        if (mBinding != null) {
            filterUtil.setOrigin(getSelectedOrigin());
            filterUtil.setSortBy(getSelectedSortBy());
            filterUtil.setSortDirection(getSortDirection());
        }

        return filterUtil;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSearch:
                onSearchClicked();
                break;
            case R.id.buttonCancel:
                onCancelClicked();
                break;
        }
    }

    @Override
    public void updateBackground(int id) {
        mBinding.filtersDialog.setBackgroundColor(getResources().getColor(id, null));
    }

    @Override
    public void updateFontSize(int size) {

    }

    @Override
    public void updateFontFamily(String family) {

    }
}
