package com.chetan.foodrecipe2.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.repositories.RecipeRepository;
import com.chetan.foodrecipe2.util.Resource;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState { CATEGORIES, RECIPES }

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    private RecipeRepository mRecipeRepository;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init() {
        if(viewState == null){
            viewState = new MutableLiveData<>();
        }
    }

    public MutableLiveData<ViewState> getViewState(){ return viewState; }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }

    public void searchRecipeApi(String query, int pageNumber){

        final LiveData<Resource<List<Recipe>>> repositorySource  =
                mRecipeRepository.searchRecipesApi(query , pageNumber);

        recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                recipes.setValue(listResource);
            }
        });
    }
}















