package com.yingenus.pocketchinese.view.holders;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HeaderAdapter extends RecyclerView.Adapter{
    private static final int HEADER=0;
    private static final int BODY=1;

    protected abstract RecyclerView.ViewHolder onCreateHeaderViewHolder(@NonNull ViewGroup parent);
    protected abstract RecyclerView.ViewHolder onCreateBodyViewHolder(@NonNull ViewGroup parent);
    protected abstract void onBindHeaderViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);
    protected abstract void onBindBodyViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);
    protected abstract int getBodyItemCount();

    public HeaderAdapter() {
        super();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0) return HEADER;
        else return BODY;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case HEADER: return onCreateHeaderViewHolder(parent);
            case BODY: return onCreateBodyViewHolder(parent);
            default: throw new RuntimeException("illegal viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position==0) onBindHeaderViewHolder(holder,position);
        else onBindBodyViewHolder(holder,position-1);
    }

    @Override
    public int getItemCount() {
        return getBodyItemCount()+1;
    }
}
