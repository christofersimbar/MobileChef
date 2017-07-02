package net.cdmsoftware.mobilechef;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.cdmsoftware.mobilechef.ui.FragmentIngredient;
import net.cdmsoftware.mobilechef.ui.FragmentStep;
import net.cdmsoftware.mobilechef.ui.FragmentVideo;
import net.cdmsoftware.mobilechef.widget.IngredientWidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.cdmsoftware.mobilechef.Utilities.setAsFavoriteRecipe;
import static net.cdmsoftware.mobilechef.data.Contract.RecipeEntry;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG_FRAGMENT_VIDEO = "tag_fragment_video";

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_view_pager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.detail_recipe_image)
    ImageView recipeImage;

    @BindView(R.id.fab)
    FloatingActionButton fabFavorite;

    private Cursor cursor;
    private long recipeId;
    private int numOfServings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //initialize Butterknife
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //retrieve URI from recipe list
        if (getIntent() != null && getIntent().getData() != null) {
            recipeId = RecipeEntry.getRecipeId(getIntent().getData());
        }

        if (findViewById(R.id.video_container) != null) {
            long stepId = 0;
            Fragment fragmentVideo = FragmentVideo.newInstance(recipeId, stepId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.video_container, fragmentVideo, TAG_FRAGMENT_VIDEO)
                    .commit();
        }

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        getLoaderManager().initLoader(3, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = RecipeEntry.buildItemUri(recipeId);
        return new CursorLoader(this,
                uri,
                RecipeEntry.RECIPE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            cursor = data;
            numOfServings = cursor.getInt(RecipeEntry.POSITION_SERVINGS);
            recipeImage.setContentDescription(cursor.getString(RecipeEntry.POSITION_NAME));
            if (findViewById(R.id.collapsing_toolbar_layout) != null) {
                CollapsingToolbarLayout collapsingToolbarLayout =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
                collapsingToolbarLayout.setTitle(cursor.getString(RecipeEntry.POSITION_NAME));
            }

            if (null != getSupportActionBar()) {
                getSupportActionBar().setTitle(cursor.getString(RecipeEntry.POSITION_NAME));
            }

            if (!cursor.getString(RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(this)
                        .load(cursor.getString(RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(recipeImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(recipeImage);
            }
            // Create an adapter that knows which fragment should be shown on each page
            DetailPagerAdapter detailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());

            // Set the adapter onto the view pager
            viewPager.setAdapter(detailPagerAdapter);

            fabFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAsFavoriteRecipe(getApplicationContext(),
                            recipeId,
                            cursor.getString(RecipeEntry.POSITION_NAME),
                            cursor.getString(RecipeEntry.POSITION_IMAGE)
                    );

                    //update widget immediately to show updated favorite recipe
                    Intent intent = new Intent(v.getContext(), IngredientWidgetProvider.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), IngredientWidgetProvider.class));
                    // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
                    // since it seems the onUpdate() is only fired on that:
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    sendBroadcast(intent);

                    //notify user
                    Toast.makeText(v.getContext(), R.string.notification_favorite_added, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    private class DetailPagerAdapter extends FragmentPagerAdapter {
        DetailPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Ingredients";
                case 1:
                    return "Steps";
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentIngredient.newInstance(recipeId, numOfServings);
                case 1:
                    return FragmentStep.newInstance(recipeId);
                default:
                    return null;
            }
        }
    }
}
