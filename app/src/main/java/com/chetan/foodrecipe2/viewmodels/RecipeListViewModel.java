package com.chetan.foodrecipe2.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.repositories.RecipeRepository;
import com.chetan.foodrecipe2.util.Resource;
import com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse;
import com.chetan.foodrecipe2.util.NetworkBoundResource;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public static final String QUERY_EXHAUSTED = "No more results.";
    public enum ViewState {CATEGORIES, RECIPES}

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>>> recipes = new MediatorLiveData<>();
    private com.chetan.foodrecipe2.repositories.RecipeRepository recipeRepository;

    // query extras
    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();

    }

    private void init(){
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }
    public LiveData<ViewState> getViewstate(){
        return viewState;
    }

    public LiveData<com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>>> getRecipes(){
        return recipes;
    }

    public int getPageNumber(){
        return pageNumber;
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(!isPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }

    public void searchNextPage(){
        if(!isQueryExhausted && !isPerformingQuery){
            pageNumber++;
            executeSearch();
        }
    }

    private void executeSearch(){
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, new Observer<com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>>>() {
            @Override
            public void onChanged(@Nullable com.chetan.foodrecipe2.util.Resource<List<com.chetan.foodrecipe2.models.Recipe>> listResource) {
                if(!cancelRequest){
                    if(listResource != null){
                        recipes.setValue(listResource);
                        if(listResource.status == com.chetan.foodrecipe2.util.Resource.Status.SUCCESS){
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if(listResource.data != null){
                                if(listResource.data.size() == 0){
                                    Log.d(TAG, "onChanged: query is exhausted...");
                                    recipes.setValue(
                                            new com.chetan.foodrecipe2.util.Resource<List<Recipe>>(
                                                    com.chetan.foodrecipe2.util.Resource.Status.ERROR,
                                                    listResource.data,
                                                    QUERY_EXHAUSTED
                                            )
                                    );
                                }
                            }
                            recipes.removeSource(repositorySource);
                        }
                        else if(listResource.status == Resource.Status.ERROR){
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            recipes.removeSource(repositorySource);
                        }
                    }
                    else{
                        recipes.removeSource(repositorySource);
                    }
                }
                else{
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }

    public void cancelSearchRequest(){
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }
}















