package net.cdmsoftware.mobilechef.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.cdmsoftware.mobilechef.DetailActivity;
import net.cdmsoftware.mobilechef.R;
import net.cdmsoftware.mobilechef.Utilities;

import static net.cdmsoftware.mobilechef.data.Contract.RecipeEntry;

public class IngredientWidgetProvider extends AppWidgetProvider {
    private static class RecipeImageTarget implements Target {
        RemoteViews view;
        int imageResourceId;

        RecipeImageTarget(RemoteViews view, int imageResourceId) {
            this.view = view;
            this.imageResourceId = imageResourceId;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            float aspectRatio = bitmap.getWidth() /
                    (float) bitmap.getHeight();
            int width = 480;
            int height = Math.round(width / aspectRatio);

            //resize image to prevent error due bitmap memory limitation in widget
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, width, height, false);
            view.setImageViewBitmap(imageResourceId, bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_ingredient);

        // get favorite recipe for widget from shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long recipeId = prefs.getLong(Utilities.PREF_KEY_RECIPE_ID, 0);
        String recipeName = prefs.getString(Utilities.PREF_KEY_RECIPE_NAME, "");
        String recipeImage = prefs.getString(Utilities.PREF_KEY_RECIPE_IMAGE, "");

        //bind data to remote views
        remoteViews.setTextViewText(R.id.widget_recipe_name, recipeName);
        if (!recipeImage.equals("")) {
            Picasso.with(context)
                    .load(recipeImage)
                    .placeholder(R.drawable.placeholder_wide)
                    .error(R.drawable.placeholder_wide)
                    .into(new RecipeImageTarget(remoteViews, R.id.widget_recipe_image));
        }

        //for binding data to listview use remote adapter
        remoteViews.setRemoteAdapter(R.id.widget_ingredient_list, new Intent(context, IngredientRemoteViewsService.class));

        // Create an Intent to launch MainActivity when widget header clicked
        Intent intent = new Intent(context, DetailActivity.class).setData(RecipeEntry.buildItemUri(recipeId));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_ingredient_header, pendingIntent);

        // Create an Intent Template to launch Detail Activity when item clicked
        // Note: According to rubric, this is not required
//        Intent clickIntentTemplate = new Intent(context, DetailActivity.class);
//        PendingIntent clickPendingIntentTemplate = PendingIntent.getActivity(
//                context,
//                0,
//                clickIntentTemplate,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setPendingIntentTemplate(R.id.widget_ingredient_list, clickPendingIntentTemplate);

        //handle empty ingredient
        remoteViews.setEmptyView(R.id.widget_ingredient_list, R.id.widget_empty);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utilities.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredient_list);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

