package net.cdmsoftware.mobilechef;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.cdmsoftware.mobilechef.widget.IngredientWidgetProvider;

public class Utilities {
    public static final String ACTION_DATA_UPDATED = "net.cdmsoftware.mobilechef.ACTION_DATA_UPDATED";

    public static boolean isFavoriteEmpty(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getLong(IngredientWidgetProvider.PREF_KEY_RECIPE_ID, 0) == 0);
    }

    public static void setAsFavoriteRecipe(Context context, long recipeId, String recipeName, String recipeImage) {
        SharedPreferences.Editor prefs = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        prefs.putLong(IngredientWidgetProvider.PREF_KEY_RECIPE_ID, recipeId);
        prefs.putString(IngredientWidgetProvider.PREF_KEY_RECIPE_NAME, recipeName);
        prefs.putString(IngredientWidgetProvider.PREF_KEY_RECIPE_IMAGE, recipeImage);
        prefs.apply();

    }
}
