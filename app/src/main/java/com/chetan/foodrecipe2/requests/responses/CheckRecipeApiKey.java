package com.chetan.foodrecipe2.requests.responses;

import com.chetan.foodrecipe2.requests.responses.RecipeResponse;
import com.chetan.foodrecipe2.requests.responses.RecipeSearchResponse;

public class CheckRecipeApiKey {

    protected static boolean isRecipeApiKeyValid(RecipeSearchResponse response){
        return response.getError() == null;
    }

    protected static boolean isRecipeApiKeyValid(RecipeResponse response){
        return response.getError() == null;
    }
}
