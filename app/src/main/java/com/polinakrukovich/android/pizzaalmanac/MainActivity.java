package com.polinakrukovich.android.pizzaalmanac;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.polinakrukovich.android.pizzaalmanac.adapter.PizzaAdapter;
import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.ActivityMainBinding;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;
import com.polinakrukovich.android.pizzaalmanac.util.FilterUtil;
import com.polinakrukovich.android.pizzaalmanac.util.PizzaUtil;
import com.polinakrukovich.android.pizzaalmanac.viewmodel.MainActivityViewModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        FilterDialogFragment.FilterListener,
        PizzaAdapter.OnPizzaSelectedListener,
        View.OnClickListener,
        NewPizzaFragment.NewPizzaListener,
        SettingsObserver {

    private static final String TAG = "MainActivity";

    private final String SHARED_PREF = "sharedPref";

    private static final int RC_SIGN_IN = 9001;

    private static final int LIMIT = 50;

    private ActivityMainBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private NewPizzaFragment mNewPizzaFragment;
    private SettingsFragment mSettingsFragment;
    private PizzaAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);

        mBinding.filterBar.setOnClickListener(this);
        mBinding.buttonClearFilter.setOnClickListener(this);
        mBinding.fabShowNewPizzaDialog.setOnClickListener(this);

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        FirebaseFirestore.setLoggingEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();

        mQuery = mFirestore.collection("pizzas")
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(LIMIT);

        mAdapter = new PizzaAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerPizzas.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerPizzas.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.recyclerPizzas.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerPizzas.setAdapter(mAdapter);

        mFilterDialog = new FilterDialogFragment();
        mNewPizzaFragment = new NewPizzaFragment();
        mSettingsFragment = new SettingsFragment();

        AppConfig.getInstance().init(getSharedPreferences(SHARED_PREF, MODE_PRIVATE));

        AppConfig.getInstance().addObserver(this);
        updateBackground(AppConfig.getInstance().getBackgroundColorId());
        updateFontSize(AppConfig.getInstance().getFontSize());
        updateFontFamily(AppConfig.getInstance().getFontFamily());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }

        onFilter(mViewModel.getFilterUtil());

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.settings:
                onSettingsOptionSelected();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish();
                } else if (response.getError() != null
                        && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.no_network_connection);
                } else {
                    showSignInErrorDialog(R.string.unknown_error);
                }
            }
        }
    }

    public void onFilterClicked() {
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(FilterUtil.getDefault());
    }

    @Override
    public void onPizzaSelected(DocumentSnapshot pizza) {

        Intent intent = new Intent(this, PizzaDetailActivity.class);
        intent.putExtra(PizzaDetailActivity.KEY_PIZZA_ID, pizza.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onFilter(FilterUtil filterUtil) {
        Query query = mFirestore.collection("pizzas");

        if (filterUtil.hasOrigin()) {
            query = query.whereEqualTo(Pizza.FIELD_ORIGIN, filterUtil.getOrigin());
        }

        if (filterUtil.hasSortBy()) {
            query = query.orderBy(filterUtil.getSortBy(), filterUtil.getSortDirection());
        }

        query = query.limit(LIMIT);

        mAdapter.setQuery(query);

        mBinding.textCurrentSearch.setText(HtmlCompat.fromHtml(filterUtil.getSearchDescription(this),
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        mBinding.textCurrentSortBy.setText(filterUtil.getOrderDescription(this));

        mViewModel.setFilterUtil(filterUtil);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void startSignIn() {
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private void onAddItemsClicked() {

        WriteBatch batch = mFirestore.batch();
        for (int i = 0; i < 10; i++) {
            DocumentReference pizzaRef = mFirestore.collection("pizzas").document();

            Pizza randomPizza = PizzaUtil.getRandom(this);

            batch.set(pizzaRef, randomPizza);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Write batch succeeded.");
                } else {
                    Log.w(TAG, "write batch failed.", task.getException());
                }
            }
        });
    }

    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.sign_in_error)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }

    public void onNewPizzaButtonClicked() {
        mNewPizzaFragment.show(getSupportFragmentManager(), NewPizzaFragment.TAG);
    }

    @Override
    public void onNewPizza(Pizza pizza) {
        WriteBatch batch = mFirestore.batch();
        DocumentReference pizzaRef = mFirestore.collection("pizzas").document();
        batch.set(pizzaRef, pizza);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Write batch succeeded.");
                } else {
                    Log.w(TAG, "write batch failed.", task.getException());
                }
            }
        });
    }

    private void onSettingsOptionSelected() {
        mSettingsFragment.show(getSupportFragmentManager(), SettingsFragment.TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterBar:
                onFilterClicked();
                break;
            case R.id.buttonClearFilter:
                onClearFilterClicked();
                break;
            case R.id.fabShowNewPizzaDialog:
                onNewPizzaButtonClicked();
        }
    }

    @Override
    public void updateBackground(int id) {
        mBinding.mainScreen.setBackgroundColor(getResources().getColor(id, null));
    }

    @Override
    public void updateFontSize(int size) {
        mBinding.noResultsMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void updateFontFamily(String family) {
        mBinding.noResultsMessage.setTypeface(Typeface.create(family, Typeface.NORMAL));
    }
}