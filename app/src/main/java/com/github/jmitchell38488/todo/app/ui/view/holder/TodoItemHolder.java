package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.listener.ItemTouchHelperViewHolder;
import com.github.jmitchell38488.todo.app.ui.listener.OnStartDragListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public View mView;
    private Context mContext;

    @BindView(R.id.list_fragment_title) TextView titleView;
    @BindView(R.id.list_fragment_description) TextView descView;
    @BindView(R.id.list_item_move_handle) ImageView moveHandle;
    @BindView(R.id.list_item_notification_wrapper) LinearLayout notificationLayout;
    @BindView(R.id.list_item_notification_pinned) TextView iconPinned;
    @BindView(R.id.list_item_notification_reminder) TextView iconReminder;
    @BindView(R.id.list_item_notification_locked) TextView iconLocked;
    @BindView(R.id.list_item_pending_remove) View removePendingView;
    @BindView(R.id.list_item_pending_complete) View completePendingView;
    @BindView(R.id.list_item_container) View itemVisibleView;

    private boolean isPendingRemoval;
    private boolean isPendingComplete;

    public TodoItemHolder(View itemView, Context context) {
        super(itemView);
        mView = itemView;
        mContext = context;
        isPendingRemoval = false;
        isPendingComplete = false;

        ButterKnife.bind(this, itemView);
    }

    public void updateView(TodoItem item) {
        // Make sure that remove pending isn't displaying
        removePendingView.setVisibility(View.GONE);
        completePendingView.setVisibility(View.GONE);
        itemVisibleView.setVisibility(View.VISIBLE);
        isPendingRemoval = false;
        isPendingComplete = false;

        iconPinned.setVisibility(View.VISIBLE);
        iconReminder.setVisibility(View.VISIBLE);

        String desc = item.getDescription();
        final boolean hasDesc = !TextUtils.isEmpty(desc);
        final boolean isPinned = item.isPinned();
        final boolean hasReminder = false;
        final boolean isLocked = item.isLocked();
        
        if (!hasDesc) {
            descView.setVisibility(View.GONE);
        } else {
            descView.setVisibility(View.VISIBLE);
        }

        // Set which notifications are visible
        if (!isPinned) {
            iconPinned.setVisibility(View.GONE);
        }

        if (!hasReminder) {
            iconReminder.setVisibility(View.GONE);
        }

        if (!isLocked) {
            iconLocked.setVisibility(View.GONE);
        }
        
        // Set title
        titleView.setText(item.getTitle());

        // Set description
        if (hasDesc && descView != null) {
            descView.setText(desc);
        }

        setItemViewCompleted(item);
    }

    public void updateViewPendingRemoval() {
        // Make sure that the item content isn't visible
        removePendingView.setVisibility(View.VISIBLE);
        completePendingView.setVisibility(View.GONE);
        itemVisibleView.setVisibility(View.GONE);
        isPendingRemoval = true;
        isPendingComplete = false;
    }

    public void updateViewPendingComplete() {
        // Make sure that the item content isn't visible
        completePendingView.setVisibility(View.VISIBLE);
        removePendingView.setVisibility(View.GONE);
        itemVisibleView.setVisibility(View.GONE);
        isPendingRemoval = false;
        isPendingComplete = true;
    }

    @Override
    public void onItemSelected() {
        itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onItemClear() {
        itemView.setBackgroundColor(0);
    }

    public void bindDragEvent(final OnStartDragListener onStartDragListener) {
        moveHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPendingRemoval) {
                    return false;
                }

                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(TodoItemHolder.this);
                }

                return false;
            }
        });
    }

    public void setItemViewCompleted(TodoItem item) {
        if (!item.isCompleted()) {
            titleView.setTextColor(mContext.getResources().getColorStateList(R.color.list_selector_text));
            titleView.setPaintFlags(titleView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            if (descView != null) {
                descView.setTextColor(mContext.getResources().getColorStateList(R.color.list_selector_text));
                descView.setPaintFlags(descView.getPaintFlags()& (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

        } else {
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleView.setTextColor(Color.LTGRAY);

            if (descView != null) {
                descView.setPaintFlags(descView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descView.setTextColor(Color.LTGRAY);
            }
        }

        if (item.isPinned() && item.isCompleted()) {
            item.setPinned(false);
        }
    }

    public boolean canMove(TodoItem source, TodoItem target) {
        if (isPendingRemoval || isPendingComplete) {
            return false;
        }

        // Can move items in only the same set
        if ((source.isPinned() && target.isPinned()) ||
                (source.isCompleted() && target.isCompleted()) ||
                (!source.isCompleted() && !source.isPinned() &&
                        !target.isCompleted() && !target.isPinned())) {
            return true;
        }

        return false;
    }

    public boolean canSwipe(TodoItem source) {
        if (source.isLocked()) {
            return false;
        }

        return true;
    }

    public View getRemovePendingView() {
        return removePendingView;
    }

    public View getCompletePendingView() {
        return completePendingView;
    }

    public View getItemVisibleView() {
        return itemVisibleView;
    }

    public boolean isPendingRemoval() {
        return isPendingRemoval;
    }

    public boolean isPendingComplete() {
        return isPendingComplete;
    }
}
