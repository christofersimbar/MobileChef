package net.cdmsoftware.mobilechef.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.cdmsoftware.mobilechef.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.cdmsoftware.mobilechef.data.Contract.RecipeEntry;

class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    RecipeAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, RecipeViewHolder recipeViewHolder);
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.recipeName.setText(cursor.getString(RecipeEntry.POSITION_NAME));
            if (!cursor.getString(RecipeEntry.POSITION_IMAGE).equals("")) {
                Picasso.with(context)
                        .load(cursor.getString(RecipeEntry.POSITION_IMAGE))
                        .placeholder(R.drawable.recipe_no_image)
                        .error(R.drawable.recipe_no_image)
                        .into(holder.recipeImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.recipe_no_image)
                        .placeholder(R.drawable.recipe_no_image)
                        .into(holder.recipeImage);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(RecipeEntry.POSITION_ID);
    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_image)
        ImageView recipeImage;

        @BindView(R.id.recipe_name)
        TextView recipeName;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onListItemClick(getAdapterPosition(), this);
        }
    }
}
