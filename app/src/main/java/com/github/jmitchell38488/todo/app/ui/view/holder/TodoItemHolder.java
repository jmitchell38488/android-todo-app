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

public class TodoItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public View mView;
    private Context mContext;

    TextView titleView;
    TextView descView;
    LinearLayout notificationLayout;
    TextView iconPinned;
    TextView iconReminder;
    ImageView moveHandle;

    View removePendingView;
    View completePendingView;
    View itemVisibleView;

    private boolean isPendingRemoval;
    private boolean isPendingComplete;

    public TodoItemHolder(View itemView, Context context) {
        super(itemView);
        mView = itemView;
        mContext = context;
        
        titleView = (TextView) itemView.findViewById(R.id.list_fragment_title);
        descView = (TextView) itemView.findViewById(R.id.list_fragment_description);
        notificationLayout = (LinearLayout) itemView.findViewById(R.id.list_item_notification_wrapper);
        iconPinned = (TextView) itemView.findViewById(R.id.list_item_notification_pinned);
        iconReminder = (TextView) itemView.findViewById(R.id.list_item_notification_reminder);
        moveHandle = (ImageView) itemView.findViewById(R.id.list_item_move_handle);

        removePendingView = itemView.findViewById(R.id.list_item_pending_remove);
        completePendingView = itemView.findViewById(R.id.list_item_pending_complete);
        itemVisibleView = itemView.findViewById(R.id.list_item_container);

        isPendingRemoval = false;
        isPendingComplete = false;
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
        
        // Set title
        titleView.setText(item.getTitle());

        // Set description
        if (hasDesc && descView != null) {
            descView.setText(desc);
        }

        setItemViewCompleted(item);
    }

    public void updateViewPendingRemoval(TodoItem item) {
        // Make sure that the item content isn't visible
        removePendingView.setVisibility(View.VISIBLE);
        completePendingView.setVisibility(View.GONE);
        itemVisibleView.setVisibility(View.GONE);
        isPendingRemoval = true;
        isPendingComplete = false;
    }

    public void updateViewPendingComplete(TodoItem item) {
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

    public View getRemovePendingView() {
        return removePendingView;
    }

    public View getCompletePendingView() {
        return completePendingView;
    }

    public boolean isPendingRemoval() {
        return isPendingRemoval;
    }

    public void setPendingRemoval(boolean pendingRemoval) {
        isPendingRemoval = pendingRemoval;
    }

    public boolean isPendingComplete() {
        return isPendingComplete;
    }

    public void setPendingComplete(boolean pendingComplete) {
        isPendingComplete = pendingComplete;
    }
}
