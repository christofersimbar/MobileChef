package net.cdmsoftware.mobilechef.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.cdmsoftware.mobilechef.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.cdmsoftware.mobilechef.data.Contract.IngredientEntry;

class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    IngredientAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, IngredientViewHolder ingredientViewHolder);
    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_ingredient, parent, false);
        return new IngredientViewHolder(item);
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.IngredientViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.ingredientQty.setText(cursor.getString(IngredientEntry.POSITION_QUANTITY));
            holder.ingredientMeasure.setText(cursor.getString(IngredientEntry.POSITION_MEASURE));
            holder.ingredientName.setText(cursor.getString(IngredientEntry.POSITION_INGREDIENT));
        }
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(IngredientEntry.POSITION_ID);
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

    class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ingredient_qty)
        TextView ingredientQty;

        @BindView(R.id.ingredient_measure)
        TextView ingredientMeasure;

        @BindView(R.id.ingredient_name)
        TextView ingredientName;

        IngredientViewHolder(View itemView) {
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
