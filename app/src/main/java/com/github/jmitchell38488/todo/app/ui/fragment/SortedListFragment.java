package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import rx.android.schedulers.AndroidSchedulers;

public class SortedListFragment extends ListFragment {

    private static final String LOG_TAG = SortedListFragment.class.getSimpleName();
    private static final String ARG_SORT = "state_sort";
    private static final String ARG_FILTER = "state_filter";

    private Sort mSort;
    private Filter mFilter;

    public static SortedListFragment newInstance(@NonNull Sort sort, @Nullable Filter filter) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SORT, sort);
        args.putSerializable(ARG_FILTER, filter);

        SortedListFragment fragment = new SortedListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mSort = args.getSerializable(ARG_SORT) != null
                    ? (Sort) args.getSerializable(ARG_SORT)
                    : null;

            mFilter = args.getSerializable(ARG_FILTER) != null
                    ? (Filter) args.getSerializable(ARG_FILTER)
                    : null;
        }

        if (savedInstanceState != null) {
            // Nothing at the moment
        } else {
            reloadContent();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubscriptions.add(mHelper.getCompletedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    int count = mAdapter.getItemCount();
                    for (int position = 0; position < count; position++) {
                        if (mAdapter.getItemId(position) == event.itemId) {
                            Log.v(LOG_TAG, "Changing complete status for item at position (" + position + ")");
                            doCompleteAction(position);
                            break;
                        }
                    }
        }));

        mSubscriptions.add(mHelper.getRemovedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    int count = mAdapter.getItemCount();
                    for (int position = 0; position < count; position++) {
                        if (mAdapter.getItemId(position) == event.itemId) {
                            Log.v(LOG_TAG, "Removing item from list at position (" + position + ")");
                            mAdapter.remove(position);
                            break;
                        }
                    }
                }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    protected final void reloadContent() {
        mAdapter.clear();
        mAdapter.add(mItemRepository.getAllItems(mSort, mFilter));
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
    }

    protected void doCompleteAction(int position) {
        TodoItem item = mAdapter.getItem(position);

        if (mPendingCompleteList.contains(item)) {
            mPendingCompleteList.remove(item);
        }

        int nPosition = getFirstCompletedPosition();

        if (!item.isCompleted()) {
            nPosition--;
        }

        item.setCompleted(!item.isCompleted());

        if (item.isCompleted()) {
            item.setPinned(false);
        }

        // No completed items yet
        if (nPosition < 0) {
            nPosition = mAdapter.getItemCount() - 1;
        }

        mAdapter.remove(position);
        mAdapter.addItem(nPosition, item);
    }

}
