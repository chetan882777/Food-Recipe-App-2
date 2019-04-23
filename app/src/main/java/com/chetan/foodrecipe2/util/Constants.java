package com.chetan.foodrecipe2.util;

public class Constants {

    public static final String BASE_URL = "https://www.food2fork.com";

    // YOU NEED YOUR OWN API KEY!!!!!!!!!!!!! https://www.food2fork.com/about/api
    public static final String API_KEY = "7e492070bb89e08a95b8a5b4c38e8eb7";

    public static final int CONNECTION_TIMEOUT = 10; // 10seconds
    public static final int READ_TIMEOUT = 10; // 10seconds
    public static final int WRITE_TIMEOUT = 10; // 10seconds

    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbeque", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbeque",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
    public static final long RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30; // 30 days
}
