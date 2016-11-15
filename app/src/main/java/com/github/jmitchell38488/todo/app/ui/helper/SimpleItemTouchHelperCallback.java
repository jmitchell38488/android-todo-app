package com.github.jmitchell38488.todo.app.ui.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to listen for {@link
 * ItemTouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;

    private final ItemTouchHelperAdapter mAdapter;

    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private Drawable backgroundRemove;
    private Drawable backgroundComplete;
    private boolean initiated;
    private Context mContext;

    private void init() {
        // Do nothing
    }

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Context context) {
        mAdapter = adapter;
        mContext = context;

        backgroundRemove = new ColorDrawable(mContext.getResources().getColor(R.color.list_item_background_delete));
        backgroundComplete = new ColorDrawable(mContext.getResources().getColor(R.color.list_item_background_complete));

        initiated = true;
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
        TodoItemHolder holder = (TodoItemHolder) viewHolder;

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
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Order of Precedence: Pinned > Not Pinned > Completed
        // Do not allow shuffle if out of order
        RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
        TodoItem sItem = adapter.getItem(source.getAdapterPosition());
        TodoItem tItem = adapter.getItem(target.getAdapterPosition());

        if (!((TodoItemHolder) source).canMove(sItem, tItem)) {
            return false;
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
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
                ((RecyclerListAdapter) mAdapter).onItemDismiss(viewHolder.getAdapterPosition());
                break;

            case ItemTouchHelper.START:
                ((RecyclerListAdapter) mAdapter).onItemComplete(viewHolder.getAdapterPosition());
                break;
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final TodoItemHolder holder = (TodoItemHolder) viewHolder;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (holder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw background
        View mView = holder.mView;
        int pLeft = mView.getLeft() + mView.getPaddingLeft();
        int pTop = mView.getTop() - mView.getPaddingTop();
        int pRight = mView.getRight() - mView.getPaddingRight();
        int pBottom = mView.getBottom() + mView.getPaddingBottom() - 2;

        // Only render on X axis movement
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0) {
                backgroundRemove.setBounds(pLeft, pTop, pRight, pBottom);
                backgroundRemove.draw(c);
            } else if (dX < 0) {
                backgroundComplete.setBounds(pLeft, pTop, pRight, pBottom);
                backgroundComplete.draw(c);
            } else {
                // Don't draw anything, we don't need a background if we aren't sliiiiiding
            }
        }

        // Force the update in the draw
        int position = recyclerView.getChildLayoutPosition(mView);
        TodoItem item = ((RecyclerListAdapter) recyclerView.getAdapter()).getItem(position);
        item.height = item.height == 0 ? pBottom - pTop - 2 : item.height;
        item.width = item.width == 0 ?pRight - pLeft : item.width;
        item.sX = item.sX == 0 ? mView.getLeft() : item.sX;
        item.sY = item.sY == 0 ? mView.getTop() : item.sY;

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            /*final float alpha = ALPHA_FULL - (float) ((Math.abs(dX) / (float) viewHolder.itemView.getWidth()) * .3);
            viewHolder.itemView.setAlpha(alpha);*/
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}