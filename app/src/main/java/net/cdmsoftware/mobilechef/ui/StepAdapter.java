package net.cdmsoftware.mobilechef.ui;

import android.annotation.SuppressLint;
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

import static net.cdmsoftware.mobilechef.data.Contract.StepEntry;

class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private Cursor cursor;
    private Context context;

    final private ListItemClickListener onClickListener;

    StepAdapter(Cursor cursor, Context context, ListItemClickListener listener) {
        this.cursor = cursor;
        this.context = context;
        this.onClickListener = listener;
    }

    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, StepViewHolder stepViewHolder);
    }

    @Override
    public StepAdapter.StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_step, parent, false);
        return new StepViewHolder(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(StepAdapter.StepViewHolder holder, int position) {
        cursor.moveToPosition(position);
        if (null != cursor) {
            holder.stepNumber.setText(Integer.toString(cursor.getInt(StepEntry.POSITION_ID)));
            holder.stepDescription.setText(cursor.getString(StepEntry.POSITION_SHORT_DESCRIPTION));
        }
    }

    @Override
    public long getItemId(int position) {
        return 1;
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

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.step_number)
        TextView stepNumber;

        @BindView(R.id.step_description)
        TextView stepDescription;

        StepViewHolder(View itemView) {
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
