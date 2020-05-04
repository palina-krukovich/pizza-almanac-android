package com.polinakrukovich.android.pizzaalmanac;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.ActivityPizzaDetailBinding;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;

public class PizzaDetailActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>,
        View.OnClickListener,
        SettingsObserver {

    private static final String TAG = "PizzaDetail";

    public static final String KEY_PIZZA_ID = "key_pizza_id";

    private ActivityPizzaDetailBinding mBinding;

    private FirebaseFirestore mFirestore;
    private DocumentReference mPizzaRef;
    private ListenerRegistration mPizzaRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPizzaDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.pizzaButtonBack.setOnClickListener(this);

        String pizzaId = getIntent().getExtras().getString(KEY_PIZZA_ID);
        if (pizzaId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_PIZZA_ID);
        }

        mFirestore = FirebaseFirestore.getInstance();

        mPizzaRef = mFirestore.collection("pizzas").document(pizzaId);

    }

    @Override
    public void onStart() {
        super.onStart();
        mPizzaRegistration = mPizzaRef.addSnapshotListener(this);
        AppConfig.getInstance().addObserver(this);
        updateBackground(AppConfig.getInstance().getBackgroundColorId());
        updateFontSize(AppConfig.getInstance().getFontSize());
        updateFontFamily(AppConfig.getInstance().getFontFamily());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPizzaRegistration != null) {
            mPizzaRegistration.remove();
            mPizzaRegistration = null;
        }
        AppConfig.getInstance().removeObserver(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "pizzas:onEvent", e);
            return;
        }

        onPizzaLoaded(snapshot.toObject(Pizza.class));
    }

    private void onPizzaLoaded(Pizza pizza) {
        mBinding.pizzaName.setText(pizza.getName());
        mBinding.pizzaRating.setRating((float) pizza.getRating());
        mBinding.pizzaOrigin.setText(pizza.getOrigin());
        mBinding.pizzaIngredients.setText(pizza.getIngredients());
        mBinding.pizzaDescription.setText(pizza.getDescription());

        Glide.with(mBinding.pizzaImage.getContext())
                .load(pizza.getPhoto())
                .into(mBinding.pizzaImage);
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pizzaButtonBack:
                onBackArrowClicked(v);
                break;
        }
    }

    @Override
    public void updateBackground(int id) {
        mBinding.pizzaDetailBody.setBackgroundColor(getResources().getColor(id, null));
    }

    @Override
    public void updateFontSize(int size) {
        mBinding.pizzaIngredients.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        mBinding.pizzaDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        mBinding.pizzaOrigin.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void updateFontFamily(String family) {
        mBinding.pizzaIngredients.setTypeface(Typeface.create(family, Typeface.NORMAL));
        mBinding.pizzaDescription.setTypeface(Typeface.create(family, Typeface.NORMAL));
        mBinding.pizzaOrigin.setTypeface(Typeface.create(family, Typeface.NORMAL));
    }
}
