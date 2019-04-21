package com.chetan.foodrecipe2.requests.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chetan.foodrecipe2.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeSearchResponse {

    @SerializedName("count")
    @Expose()
    private int count;

    @SerializedName("recipes")
    @Expose()
    private List<com.chetan.foodrecipe2.models.Recipe> recipes;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    public int getCount() {
        return count;
    }

    @Nullable
    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", recipes=" + recipes +
                ", error='" + error + '\'' +
                '}';
    }
}
