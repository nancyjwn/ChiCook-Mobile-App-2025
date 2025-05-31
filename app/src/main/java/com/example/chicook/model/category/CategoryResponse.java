package com.example.chicook.model.category;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryResponse {

    @SerializedName("categories")
    private List<Category> categories;

    // Getter
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}

