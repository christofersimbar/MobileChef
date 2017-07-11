package net.cdmsoftware.mobilechef;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import net.cdmsoftware.mobilechef.widget.IngredientWidgetProvider;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utilities {
    public static final String ACTION_DATA_UPDATED = "net.cdmsoftware.mobilechef.ACTION_DATA_UPDATED";
    public static final String PREF_KEY_RECIPE_ID = "widget_recipe_id";
    public static final String PREF_KEY_RECIPE_NAME = "widget_recipe_name";
    public static final String PREF_KEY_RECIPE_IMAGE = "widget_recipe_image";

    public class ApiResponseStatus {
        public static final int NONE = 0;
        public static final int ERROR = 1;
        public static final int SUCCESS = 2;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isFavoriteEmpty(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getLong(PREF_KEY_RECIPE_ID, 0) == 0);
    }

    public static void setAsFavoriteRecipe(Context context, long recipeId, String recipeName, String recipeImage) {
        SharedPreferences.Editor prefs = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        prefs.putLong(PREF_KEY_RECIPE_ID, recipeId);
        prefs.putString(PREF_KEY_RECIPE_NAME, recipeName);
        prefs.putString(PREF_KEY_RECIPE_IMAGE, recipeImage);
        prefs.apply();

    }
}
