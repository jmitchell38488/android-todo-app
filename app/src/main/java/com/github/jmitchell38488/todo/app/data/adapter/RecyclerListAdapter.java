package com.github.jmitchell38488.todo.app.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.helper.ItemTouchHelperAdapter;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<TodoItemHolder>
        implements ItemTouchHelperAdapter, TodoItemHolder.OnCompleteClickListener {

    private TodoStorage mTodoStorage;
    private List<TodoItem> mItems;
    private Context mContext;
    private OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, TodoStorage todoStorage, OnStartDragListener dragStartListener) {
        mContext = context;
        mTodoStorage = todoStorage;
        mDragStartListener = dragStartListener;

        mItems = mTodoStorage.getTodos();
    }

    @Override
    public TodoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_fragment_notifications, parent, false);
        TodoItemHolder holder = new TodoItemHolder(view, mContext);
        holder.attachClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TodoItemHolder holder, final int position) {
        holder.updateView(mItems.get(position));

        // Start a drag whenever the handle view it touched
        holder.bindDragEvent(mDragStartListener);

        holder.completeHandle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.onClick();
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onCompleteClick() {
        //TodoItemSorter.sort(mItems);
        //notifyDataSetChanged();
        //mTodoStorage.saveTodos(mItems);
    }
}