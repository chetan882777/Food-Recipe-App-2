package com.chetan.foodrecipe2.viewmodels;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState { CATEGORIES, RECIPES }

    private MutableLiveData<ViewState> viewState;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        if(viewState == null){
            viewState = new MutableLiveData<>();
        }
    }

    public MutableLiveData<ViewState> getViewState(){ return viewState; }

}















