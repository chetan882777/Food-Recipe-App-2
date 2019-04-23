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

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState { CATEGORIES, RECIPES }
    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();


    // query extras
    private boolean isQueryExhausted;
    private String query;
    private int pageNumber;
    private boolean isPerformingQuery;



    private RecipeRepository mRecipeRepository;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init() {
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public MutableLiveData<ViewState> getViewState(){ return viewState; }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public int getPageNumber() {
        return pageNumber;
    }



    public void searchRecipeApi(String query, int pageNumber){

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
        if(pageNumber != 0){
            searchRecipeApi(query , pageNumber++);
        }
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    private void executeSearch(){
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);

        final LiveData<Resource<List<Recipe>>> repositorySource = mRecipeRepository.searchRecipesApi(query , pageNumber);

        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    recipes.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS){
                        isPerformingQuery = false;
                        if(listResource.data != null){
                            if(listResource.data.size() == 0){
                                Log.d(TAG, "onChanged:  query is Exhausted...");
                                recipes.setValue(new Resource<List<Recipe>>(
                                        Resource.Status.ERROR,
                                        listResource.data,
                                        QUERY_EXHAUSTED
                                ));
                                isPerformingQuery = false;
                            }
                        }
                        recipes.removeSource(repositorySource);

                    }else if(listResource.status == Resource.Status.ERROR){
                        isPerformingQuery = false;
                        recipes.removeSource(repositorySource);
                    }
                }
                else{
                    recipes.removeSource(repositorySource);
                }
            }
        });
    }
}















