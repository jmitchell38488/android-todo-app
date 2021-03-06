package com.github.jmitchell38488.todo.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import rx.functions.Action1;

public abstract class StandardAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Action1<List<T>> {

    protected static final int VIEW_TYPE_ITEM = 1;

    @NonNull protected final LayoutInflater mInflater;
    @NonNull protected List<T> mItems;
    @NonNull protected Context mContext;

    public StandardAdapter(@NonNull Context context, @NonNull List<T> items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public void call(@NonNull List<T> newItems) {
        add(newItems);
    }

    public void set(@NonNull List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void add(@NonNull List<T> newItems) {
        if (!newItems.isEmpty()) {
            int currentSize = mItems.size();
            int amountInserted = newItems.size();

            mItems.addAll(newItems);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    public void remove(int position) {
        if (!mItems.isEmpty()) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    public List<T> getItems() {
        return mItems;
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public void clear() {
        if (!mItems.isEmpty()) {
            mItems.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateItemHolder(parent, viewType);
    }

    protected abstract VH onCreateItemHolder(ViewGroup parent, int viewType);

}