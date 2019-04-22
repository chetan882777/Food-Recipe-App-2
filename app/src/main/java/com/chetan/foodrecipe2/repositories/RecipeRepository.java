package com.chetan.foodrecipe2.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chetan.foodrecipe2.AppExecutors;
import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.persistence.RecipeDao;
import com.chetan.foodrecipe2.persistence.RecipeDatabase;
import com.chetan.foodrecipe2.requests.ServiceGenerator;
import com.chetan.foodrecipe2.requests.responses.ApiResponse;
import com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse;
import com.chetan.foodrecipe2.util.Constants;
import com.chetan.foodrecipe2.util.NetworkBoundResource;
import com.chetan.foodrecipe2.util.Resource;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    public static RecipeRepository getInstance(Context context){
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }


    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull RecipeSearchResponse item) {

            }

            @Override
            public boolean shouldFetch(@Nullable List<Recipe> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi()
                        .searchRecipe(
                                Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }

        }.getAsLiveData();
    }
}






