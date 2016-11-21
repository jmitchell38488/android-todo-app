package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemEditHolder;
import com.github.jmitchell38488.todo.app.util.ItemUtility;

import javax.inject.Inject;

public class EditItemFragment extends Fragment {

    private View mView;
    private TodoItem mItem;
    private String mItemHash;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        TodoItem item = null;

        if (arguments != null && arguments.getParcelable(
                ListFragment.ActivityListClickListener.ARG_TODOITEM) != null) {
            item = arguments.getParcelable(ListFragment.ActivityListClickListener.ARG_TODOITEM);
        }

        if (item == null) {
            item = new TodoItem();
        }

        mItem = item;
        mItemHash = ItemUtility.md5(item.toString());
        mView = inflater.inflate(R.layout.fragment_edit_item, container, false);
        TodoItemEditHolder viewHolder = new TodoItemEditHolder(mView, getActivity(), item);
        mView.setTag(viewHolder);
        return mView;
    }

    public TodoItem getUpdatedTodoItem() {
        TodoItemEditHolder holder = (TodoItemEditHolder) mView.getTag();
        mItem.setTitle(holder.titleView.getText().toString());
        mItem.setDescription(holder.descriptionView.getText().toString());
        mItem.setPinned(holder.pinnedSwitch.isChecked());
        mItem.setCompleted(holder.completedSwitch.isChecked());

        return mItem;
    }

    public String getItemHash() {
        return mItemHash;
    }

}
