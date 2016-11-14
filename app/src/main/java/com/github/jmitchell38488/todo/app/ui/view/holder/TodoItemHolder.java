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
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.ui.helper.ItemTouchHelperViewHolder;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;

import butterknife.ButterKnife;

public class TodoItemHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public interface OnCompleteClickListener {
        void onCompleteClick();
    }

    public View mView;

    private OnCompleteClickListener mCompleteClickListener;
    private Context mContext;
    private TodoItem mItem;

    TextView titleView;
    TextView descView;
    LinearLayout notificationLayout;
    TextView iconPinned;
    TextView iconReminder;
    public ImageView completeHandle;
    ImageView moveHandle;

    public TodoItemHolder(View itemView, Context context) {
        super(itemView);
        mView = itemView;
        mContext = context;
        
        titleView = (TextView) itemView.findViewById(R.id.list_fragment_title);
        descView = (TextView) itemView.findViewById(R.id.list_fragment_description);
        notificationLayout = (LinearLayout) itemView.findViewById(R.id.list_item_notification_wrapper);
        iconPinned = (TextView) itemView.findViewById(R.id.list_item_notification_pinned);
        iconReminder = (TextView) itemView.findViewById(R.id.list_item_notification_reminder);
        completeHandle = (ImageView) itemView.findViewById(R.id.list_item_complete_handle);
        moveHandle = (ImageView) itemView.findViewById(R.id.list_item_move_handle);
    }

    public void attachClickListener(OnCompleteClickListener listener) {
        mCompleteClickListener = listener;
    }

    public void updateView(TodoItem item) {
        if (mItem == null) {
            mItem = item;
        }

        String desc = item.getDescription();
        final boolean hasDesc = !TextUtils.isEmpty(desc);
        final boolean isCompleted = item.isCompleted();
        final boolean isPinned = item.isPinned();
        final boolean hasNotifications = isPinned ? true : false;
        final boolean hasReminder = false;

        ButterKnife.bind(this, mView);
        
        // Set visibility for various elements
        if (hasNotifications) {
            notificationLayout.setVisibility(View.VISIBLE);
        } else {
            notificationLayout.setVisibility(View.GONE);
        }
        
        if (!hasDesc) {
            descView.setVisibility(View.GONE);
        } else {
            descView.setVisibility(View.VISIBLE);
        }

        // Set which notifications are visible
        if (hasNotifications) {
            if (!isPinned) {
                iconPinned.setVisibility(View.GONE);
            }

            if (!hasReminder) {
                iconReminder.setVisibility(View.GONE);
            }
        }
        
        // Set title
        titleView.setText(item.getTitle());

        // Set description
        if (hasDesc && descView != null) {
            descView.setText(desc);
        }

        // Set or remove the strike through for completed
        if (isCompleted) {
            completeHandle.setBackgroundResource(R.drawable.list_item_category_selected);

            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleView.setTextColor(Color.LTGRAY);

            if (hasDesc) {
                descView.setPaintFlags(descView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descView.setTextColor(Color.LTGRAY);
            }
        }

        if (isCompleted) {
            completeHandle.setBackgroundResource(R.drawable.list_item_category_selected);
        }
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
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(TodoItemHolder.this);
                }
                return false;
            }
        });
    }

    public void onClick() {
        mItem.setCompleted(!mItem.isCompleted());

        if (mItem.isCompleted()) {
            mItem.setPinned(false);
        }

        setItemViewCompleted();

        if (mCompleteClickListener != null) {
            mCompleteClickListener.onCompleteClick();
        }
    }

    public void setItemViewCompleted() {
        if (!mItem.isCompleted()) {
            titleView.setTextColor(mContext.getResources().getColorStateList(R.color.list_selector_text));
            titleView.setPaintFlags(titleView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            if (descView != null) {
                descView.setTextColor(mContext.getResources().getColorStateList(R.color.list_selector_text));
                descView.setPaintFlags(descView.getPaintFlags()& (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            completeHandle.setBackgroundResource(R.drawable.list_item_category_selector);

        } else {
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleView.setTextColor(Color.LTGRAY);

            if (descView != null) {
                descView.setPaintFlags(descView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descView.setTextColor(Color.LTGRAY);
            }

            completeHandle.setBackgroundResource(R.drawable.list_item_category_selected);
        }

        if (mItem.isPinned() && mItem.isCompleted()) {
            mItem.setPinned(false);

            if (notificationLayout != null) {
                notificationLayout.setVisibility(View.GONE);
            }
        }
    }

}
