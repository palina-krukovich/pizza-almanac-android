package com.polinakrukovich.android.pizzaalmanac.adapter;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.polinakrukovich.android.pizzaalmanac.config.AppConfig;
import com.polinakrukovich.android.pizzaalmanac.databinding.ItemPizzaBinding;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;
import com.polinakrukovich.android.pizzaalmanac.observer.SettingsObserver;

public class PizzaAdapter extends FirestoreAdapter<PizzaAdapter.ViewHolder> {
    public interface OnPizzaSelectedListener {
        void onPizzaSelected(DocumentSnapshot pizza);
    }

    private OnPizzaSelectedListener mListener;

    public PizzaAdapter(Query query, OnPizzaSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPizzaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements SettingsObserver {

        private ItemPizzaBinding binding;

        public ViewHolder(ItemPizzaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnPizzaSelectedListener listener) {

            Pizza pizza = snapshot.toObject(Pizza.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(binding.pizzaItemImage.getContext())
                    .load(pizza.getPhoto())
                    .into(binding.pizzaItemImage);

            binding.pizzaItemName.setText(pizza.getName());
            binding.pizzaItemRating.setRating((float) pizza.getRating());
            binding.pizzaItemOrigin.setText(pizza.getOrigin());
            binding.pizzaItemIngredients.setText(pizza.getIngredients());
            binding.pizzaItemDescription.setText(pizza.getDescription());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onPizzaSelected(snapshot);
                    }
                }
            });

            AppConfig.getInstance().addObserver(this);
            updateFontSize(AppConfig.getInstance().getFontSize());
            updateFontFamily(AppConfig.getInstance().getFontFamily());
        }

        @Override
        public void updateBackground(int id) {

        }

        @Override
        public void updateFontSize(int size) {
            binding.pizzaItemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            binding.pizzaItemOrigin.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            binding.pizzaItemIngredients.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            binding.pizzaItemDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }

        @Override
        public void updateFontFamily(String family) {
            binding.pizzaItemName.setTypeface(Typeface.create(family, Typeface.NORMAL));
            binding.pizzaItemOrigin.setTypeface(Typeface.create(family, Typeface.NORMAL));
            binding.pizzaItemIngredients.setTypeface(Typeface.create(family, Typeface.NORMAL));
            binding.pizzaItemDescription.setTypeface(Typeface.create(family, Typeface.NORMAL));
        }
    }
}
