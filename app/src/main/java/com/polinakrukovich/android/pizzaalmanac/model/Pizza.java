package com.polinakrukovich.android.pizzaalmanac.model;


public class Pizza {
    public static final String FIELD_ORIGIN = "origin";
    public static final String FIELD_RATING = "rating";
    public static final String FIELD_NAME = "name";

    private String name;
    private String origin;
    private String ingredients;
    private String description;
    private String photo;
    private double rating;

    public Pizza() {}

    public Pizza(String name, String origin, String ingredients, String description, String photo, double rating) {
        this.name = name;
        this.origin = origin;
        this.ingredients = ingredients;
        this.description = description;
        this.photo = photo;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
    }

    public double getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
