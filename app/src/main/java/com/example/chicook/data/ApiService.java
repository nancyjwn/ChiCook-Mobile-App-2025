package com.example.chicook.data;

import com.example.chicook.model.MealResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search.php")
    Call<MealResponse> searchMeals(@Query("s") String mealName);

    @GET("filter.php")
    Call<MealResponse> searchMealsByArea(@Query("a") String area);
}

