package com.example.chicook.data;

import com.example.chicook.model.meal.MealResponse;
import com.example.chicook.model.category.CategoryResponse;
import com.example.chicook.model.randomMeals.RandomMealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String mealName);

    @GET("filter.php")
    Call<MealResponse> searchMealsByArea(@Query("a") String area);

    @GET("categories.php")
    Call<CategoryResponse> categoryMeals(@Query("") String categoryName);

    @GET("random.php")
    Call<RandomMealResponse> randomMeals(@Query("") String randomMeal);  // Mengembalikan satu meal acak
}

