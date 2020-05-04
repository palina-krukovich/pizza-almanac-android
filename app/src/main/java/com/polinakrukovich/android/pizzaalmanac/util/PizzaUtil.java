package com.polinakrukovich.android.pizzaalmanac.util;

import android.content.Context;

import com.polinakrukovich.android.pizzaalmanac.R;
import com.polinakrukovich.android.pizzaalmanac.model.Pizza;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PizzaUtil {
    private static final String TAG = "PizzaUtil";

    public static Pizza getRandom(Context context) {
        Pizza pizza = new Pizza();
        Random random = new Random();

        String[] nameFirstWords = context.getResources().getStringArray(R.array.name_first_words_random);
        String[] nameSecondWords = context.getResources().getStringArray(R.array.name_second_words_random);
        String[] origins = context.getResources().getStringArray(R.array.origins);
        origins = Arrays.copyOfRange(origins, 1, origins.length);
        String[] ingredients = context.getResources().getStringArray(R.array.ingredients_random);
        String[] photo_urls = context.getResources().getStringArray(R.array.photo_urls_random);
        String description = context.getResources().getString(R.string.description_random);

        pizza.setName(getRandomName(nameFirstWords, nameSecondWords, random));
        pizza.setOrigin(getRandomString(origins, random));
        pizza.setIngredients(getRandomIngredients(ingredients, random));
        pizza.setDescription(description);
        pizza.setPhoto(getRandomString(photo_urls, random));
        pizza.setRating(random.nextDouble() * 5.0);

        return pizza;
    }

    private static String getRandomName(String[] nameFirstWords, String[] nameSecondWords, Random random) {
        return getRandomString(nameFirstWords, random) + " "
                + getRandomString(nameSecondWords, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static String getRandomIngredients(String[] ingredients, Random random) {
        List<String> listIngredients = Arrays.asList(ingredients);
        Collections.shuffle(listIngredients);
        listIngredients = listIngredients.subList(0, random.nextInt(7));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listIngredients.size(); i++) {
            sb.append(listIngredients.get(i));
            if (i < listIngredients.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
