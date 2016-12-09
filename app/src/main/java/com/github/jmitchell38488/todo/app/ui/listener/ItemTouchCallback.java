package com.github.jmitchell38488.todo.app.ui.listener;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.ui.decoration.ItemTouchDecorator;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

public class ItemTouchCallback extends ItemTouchDecorator {

    private static final String LOG_TAG = ItemTouchCallback.class.getSimpleName();

    private final ItemTouchListener mItemTouchListener;

    public ItemTouchCallback(ItemTouchListener itemTouchListener, Context context) {
        super(context);
        mItemTouchListener = itemTouchListener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final TodoItemHolder holder = (TodoItemHolder) viewHolder;
        final TodoItem item = ((RecyclerListAdapter) recyclerView.getAdapter()).getItem(holder.getAdapterPosition());

        // Do nothing if this view holder is pending removal
        if (holder.isPendingRemoval() || holder.isPendingComplete()) {
            return 0;
        }

        // Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = item.isLocked() ? ItemTouchHelper.START : ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
        TodoItem sItem = adapter.getItem(source.getAdapterPosition());
        TodoItem tItem = adapter.getItem(target.getAdapterPosition());
        if (!((TodoItemHolder) source).canMove(sItem, tItem)) {
            return false;
        }

        return true;
    }

    @Override
    public void onMoved(final RecyclerView recyclerView,
                        final RecyclerView.ViewHolder source, int fromPos,
                        final RecyclerView.ViewHolder target, int toPos, int x, int y) {

        super.onMoved(recyclerView, source, fromPos, target, toPos, x, y);

        // Notify the adapter of the move
        Log.d(LOG_TAG, String.format("Trigger item move: %d => %d",
                source.getAdapterPosition(), target.getAdapterPosition()));
        mItemTouchListener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        TodoItemHolder holder = (TodoItemHolder) viewHolder;

        // Do nothing if this view holder is pending removal
        if (holder.isPendingRemoval() || holder.isPendingComplete()) {
            return;
        }

        switch (direction) {
            case ItemTouchHelper.END:
                mItemTouchListener.onItemSwipeRight(viewHolder.getAdapterPosition());
                break;

            case ItemTouchHelper.START:
                mItemTouchListener.onItemSwipeLeft(viewHolder.getAdapterPosition());
                break;
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
}