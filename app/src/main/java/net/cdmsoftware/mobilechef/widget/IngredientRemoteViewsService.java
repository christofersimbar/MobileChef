package net.cdmsoftware.mobilechef.widget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.cdmsoftware.mobilechef.R;
import net.cdmsoftware.mobilechef.Utilities;

import static net.cdmsoftware.mobilechef.data.Contract.IngredientEntry;

public class IngredientRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //final long recipeId = intent.getLongExtra(ARG_EXTRAS_RECIPE_ID, 0);

        return new RemoteViewsFactory() {
            Cursor cursor;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                // get favorite recipe for widget from shared preferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                long recipeId = prefs.getLong(Utilities.PREF_KEY_RECIPE_ID, 0);
                Log.v("MobileChef", String.valueOf(recipeId));

                //close current cursor if exists
                if (cursor != null) {
                    cursor.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                cursor = getContentResolver().query(
                        IngredientEntry.buildDirUri(recipeId),
                        IngredientEntry.INGREDIENT_COLUMNS.toArray(new String[]{}),
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                //get the widget remote view
                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_ingredient_list_item);

                //start filling data
                remoteViews.setTextViewText(R.id.ingredient_qty, cursor.getString(IngredientEntry.POSITION_QUANTITY));
                remoteViews.setTextViewText(R.id.ingredient_measure, cursor.getString(IngredientEntry.POSITION_MEASURE));
                remoteViews.setTextViewText(R.id.ingredient_name, cursor.getString(IngredientEntry.POSITION_INGREDIENT));

                //setup intent for launching detail activity
                // Note: According to rubric, this is not required
//                final Intent fillInIntent = new Intent();
//                fillInIntent.setData(RecipeEntry.buildItemUri(cursor.getLong(IngredientEntry.POSITION_RECIPE_ID)));
//                remoteViews.setOnClickFillInIntent(R.id.widget_ingredient_list_item, fillInIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return 1;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
