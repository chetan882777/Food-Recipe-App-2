package com.chetan.foodrecipe2.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.repositories.RecipeRepository;
import com.chetan.foodrecipe2.util.Resource;


public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return mRecipeRepository.searchRecipeApi(recipeId);
    }


}





















