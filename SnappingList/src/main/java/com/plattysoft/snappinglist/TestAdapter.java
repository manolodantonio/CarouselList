package com.plattysoft.snappinglist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    public interface RecyclerItemClickListener{
        void onItemClick(View view, int position);
    }

    private static final int VIEW_TYPE_PADDING = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private final int mNumItems;
    private int checkPointReached = -1;
    private RecyclerItemClickListener itemClickListener;

    public TestAdapter(int numItems) {
        mNumItems = numItems;
    }

    @Override
    public int getItemCount() {
        return mNumItems + 2; // We have to add 2 paddings
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == getItemCount()-1) {
            return VIEW_TYPE_PADDING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_padding, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            if (position <= checkPointReached) {
                holder.itemView.setEnabled(false);
            }
            // Binding section
            holder.text.setText(String.valueOf(position));
        }
    }

    public RecyclerItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public int getCheckPointReached() {
        return checkPointReached;
    }

    public void setCheckPointReached(int checkPointReached) {
        this.checkPointReached = checkPointReached;
    }

    /////////////////

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            text = (TextView) itemView.findViewById(R.id.item_text);
        }



        @Override
        public void onClick(View view) {
            if (itemClickListener == null) return;

            itemClickListener.onItemClick(view, this.getAdapterPosition());
        }
    }

}
