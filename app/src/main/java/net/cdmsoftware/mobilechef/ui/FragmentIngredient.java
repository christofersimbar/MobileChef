package net.cdmsoftware.mobilechef.ui;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import net.cdmsoftware.mobilechef.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.cdmsoftware.mobilechef.data.Contract.IngredientEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentIngredient extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, IngredientAdapter.ListItemClickListener {
    public static final String ARG_RECIPE_ID = "recipeId";
    public static final String ARG_NUM_OF_SERVINGS = "numOfServings";
    private long recipeId;
    private int numOfServings;

    private IngredientAdapter ingredientAdapter;
    private int position = RecyclerView.NO_POSITION;

    @BindView(R.id.ingredient_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.servings_control)
    SeekBar servingsControl;

    @BindView(R.id.servings_quantity)
    TextView servingsQuantity;

    public FragmentIngredient() {
        // Required empty public constructor
    }

    public static FragmentIngredient newInstance(long recipeId, int numOfServings) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_RECIPE_ID, recipeId);
        arguments.putInt(ARG_NUM_OF_SERVINGS, numOfServings);
        FragmentIngredient fragment = new FragmentIngredient();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //retrieve recipe id from detail activity
        if (getArguments().containsKey(ARG_RECIPE_ID)) {
            recipeId = getArguments().getLong(ARG_RECIPE_ID);
            numOfServings = getArguments().getInt(ARG_NUM_OF_SERVINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);

        //initialize ButterKnife library
        ButterKnife.bind(this, rootView);

        ingredientAdapter = new IngredientAdapter(null, getActivity(), this);

        recyclerView.setAdapter(ingredientAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));

        servingsQuantity.setText(String.valueOf(numOfServings));
        servingsControl.setProgress(numOfServings);
        servingsControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int position;
            int oldServings;
            int newServings;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                position = progressValue + 1;
                servingsQuantity.setText(String.valueOf(position));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                oldServings = Integer.valueOf(servingsQuantity.getText().toString());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                newServings = position;
                updateServings(oldServings, newServings);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(1, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = IngredientEntry.buildDirUri(recipeId);
        return new CursorLoader(getActivity(),
                uri,
                IngredientEntry.INGREDIENT_COLUMNS.toArray(new String[]{}),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ingredientAdapter.swapCursor(data);
        if (position == RecyclerView.NO_POSITION) position = 0;
        recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ingredientAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, IngredientAdapter.IngredientViewHolder ingredientViewHolder) {
        //
    }

    private void updateServings(int oldServings, int newServings) {
        getActivity().getContentResolver().update(
                IngredientEntry.buildServingsUri(recipeId, oldServings, newServings),
                null,
                null,
                null);
    }
}
