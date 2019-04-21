package com.chetan.foodrecipe2.requests.responses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chetan.foodrecipe2.models.Recipe;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private com.chetan.foodrecipe2.models.Recipe recipe;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public Recipe getRecipe(){
        return recipe;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }
}
