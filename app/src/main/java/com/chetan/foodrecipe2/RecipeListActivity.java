package com.chetan.foodrecipe2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;


import com.chetan.foodrecipe2.adapters.OnRecipeListener;
import com.chetan.foodrecipe2.adapters.RecipeRecyclerAdapter;
import com.chetan.foodrecipe2.models.Recipe;
import com.chetan.foodrecipe2.util.Resource;
import com.chetan.foodrecipe2.util.Testing;
import com.chetan.foodrecipe2.util.VerticalSpacingItemDecorator;
import com.chetan.foodrecipe2.viewmodels.RecipeListViewModel;

import java.util.List;

import static com.chetan.foodrecipe2.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mSearchView = findViewById(R.id.search_view);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: Status! " + listResource.status);

                    if(listResource.data != null){
                        switch (listResource.status){
                            case LOADING:{
                                if(mRecipeListViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                }else{
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }
                            case ERROR:{
                                Log.e(TAG, "onChanged: cannot refresh cache.");
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                                Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);

                                Toast.makeText(RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            }
                            case SUCCESS:{
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status SUCCESS, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setRecipes(listResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });

        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable RecipeListViewModel.ViewState viewState) {
                if(viewState  != null){
                    switch (viewState){
                        case CATEGORIES: {
                            displaySearchCategories();
                            break;
                        }

                        case RECIPES:{
                            break;
                        }
                    }
                }
            }
        });
    }

    private void searchRecipeApi(String query){
        mRecipeListViewModel.searchRecipeApi(query, 1);
    }

    private void displaySearchCategories() {
        mAdapter.displaySearchCategories();
    }

    private void initRecyclerView(){
        mAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchRecipeApi(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipeApi(category);
    }

}

















