package com.example.chicook.model.randomMeals;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RandomMealResponse {

    @SerializedName("meals")
    private List<RandomMeal> meals;

    public List<RandomMeal> getMeals() {
        return meals;
    }

    public void setMeals(List<RandomMeal> meals) {
        this.meals = meals;
    }
}
