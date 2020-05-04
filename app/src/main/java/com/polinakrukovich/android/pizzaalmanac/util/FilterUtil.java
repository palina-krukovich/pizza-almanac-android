package com.polinakrukovich.android.pizzaalmanac.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.firestore.Query;
import com.polinakrukovich.android.pizzaalmanac.R;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;

public class FilterUtil {
    private String origin = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public FilterUtil() {}

    public static FilterUtil getDefault() {
        FilterUtil filterUtil = new FilterUtil();
        filterUtil.setSortBy(Pizza.FIELD_RATING);
        filterUtil.setSortDirection(Query.Direction.DESCENDING);

        return filterUtil;
    }

    public boolean hasOrigin() {
        return !(TextUtils.isEmpty(origin));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder sb = new StringBuilder();

        if (origin == null) {
            sb.append("<b>");
            sb.append(context.getString(R.string.all_pizzas));
            sb.append("</b>");
        }

        if (origin != null) {
            sb.append("<b>");
            sb.append(origin);
            sb.append("</b>");
        }

        return sb.toString();
    }

    public String getOrderDescription(Context context) {
        if (Pizza.FIELD_RATING.equals(sortBy)) {
            return context.getString(R.string.sorted_by_rating);
        } else {
            return context.getString(R.string.sorted_by_name);
        }
    }
}
