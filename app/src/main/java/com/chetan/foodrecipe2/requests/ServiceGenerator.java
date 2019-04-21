package com.chetan.foodrecipe2.requests;

import com.chetan.foodrecipe2.util.LiveDataCallAdapterFactory;
import com.chetan.foodrecipe2.util.Constants;
import com.chetan.foodrecipe2.util.LiveDataCallAdapter;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.chetan.foodrecipe2.util.Constants.CONNECTION_TIMEOUT;
import static com.chetan.foodrecipe2.util.Constants.READ_TIMEOUT;
import static com.chetan.foodrecipe2.util.Constants.WRITE_TIMEOUT;

public class ServiceGenerator {

    private static OkHttpClient client = new OkHttpClient.Builder()

            // establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)

            .build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(com.chetan.foodrecipe2.util.Constants.BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static com.chetan.foodrecipe2.requests.RecipeApi recipeApi = retrofit.create(com.chetan.foodrecipe2.requests.RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
