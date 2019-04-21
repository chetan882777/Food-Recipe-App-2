package com.chetan.foodrecipe2.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chetan.foodrecipe2.AppExecutors;
import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.persistence.RecipeDao;
import com.chetan.foodrecipe2.persistence.RecipeDatabase;
import com.chetan.foodrecipe2.requests.ServiceGenerator;
import com.chetan.foodrecipe2.requests.responses.ApiResponse;
import com.chetan.foodrecipe2.requests.responses.RecipeResponse;
import com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse;
import com.chetan.foodrecipe2.util.Constants;
import com.chetan.foodrecipe2.util.NetworkBoundResource;
import com.chetan.foodrecipe2.util.Resource;

import java.util.List;

public class RecipeRepository {

    private static final String TAG = "RecipeRepository";

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


    public LiveData<com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new com.chetan.foodrecipe2.util.NetworkBoundResource<List<com.chetan.foodrecipe2.models.Recipe>, com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse>(com.chetan.foodrecipe2.AppExecutors.getInstance()){

            @Override
            protected void saveCallResult(@NonNull com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse item) {
                if(item.getRecipes() != null){ // recipe list will be null if the api key is expired

                    com.chetan.foodrecipe2.models.Recipe[] recipes = new com.chetan.foodrecipe2.models.Recipe[item.getRecipes().size()];

                    int index = 0;
                    for(long rowid: recipeDao.insertRecipes((com.chetan.foodrecipe2.models.Recipe[]) (item.getRecipes().toArray(recipes)))){
                        if(rowid == -1){
                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in the cache");
                            // if the recipe already exists... I don't want to set the ingredients or timestamp b/c
                            // they will be erased
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<com.chetan.foodrecipe2.models.Recipe> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<com.chetan.foodrecipe2.models.Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<com.chetan.foodrecipe2.requests.responses.ApiResponse<RecipeSearchResponse>> createCall() {
                return com.chetan.foodrecipe2.requests.ServiceGenerator.getRecipeApi()
                        .searchRecipe(
                                com.chetan.foodrecipe2.util.Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<com.chetan.foodrecipe2.models.Recipe>> searchRecipesApi(final String recipeId){
        return new NetworkBoundResource<com.chetan.foodrecipe2.models.Recipe, com.chetan.foodrecipe2.requests.responses.RecipeResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull com.chetan.foodrecipe2.requests.responses.RecipeResponse item) {

                // will be null if API key is expired
                if(item.getRecipe() != null){
                    item.getRecipe().setTimestamp((int)(System.currentTimeMillis() / 1000));
                    recipeDao.insertRecipe(item.getRecipe());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable com.chetan.foodrecipe2.models.Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());
                int currentTime = (int)(System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: current time: " + currentTime);
                int lastRefresh = data.getTimestamp();
                Log.d(TAG, "shouldFetch: last refresh: " + lastRefresh);
                Log.d(TAG, "shouldFetch: it's been " + ((currentTime - lastRefresh) / 60 / 60 / 24) +
                        " days since this recipe was refreshed. 30 days must elapse before refreshing. ");
                if((currentTime - data.getTimestamp()) >= com.chetan.foodrecipe2.util.Constants.RECIPE_REFRESH_TIME){
                    Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + true);
                    return true;
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE?! " + false);
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Recipe> loadFromDb() {
                return recipeDao.getRecipe(recipeId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().getRecipe(
                        Constants.API_KEY,
                        recipeId
                );
            }
        }.getAsLiveData();
    }
}












