package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import rx.android.schedulers.AndroidSchedulers;

public class SortedListFragment extends ListFragment {

    private static final String LOG_TAG = SortedListFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubscriptions.add(mHelper.getCompletedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    int count = mAdapter.getItemCount();
                    for (int position = 0; position < count; position++) {
                        if (mAdapter.getItemId(position) == event.itemId) {
                            TodoItem item = mAdapter.getItem(position);
                            if (item.isCompleted()) {
                                Log.d(LOG_TAG, String.format("%s added to the complete list!", item.getTitle()));
                            } else {
                                Log.d(LOG_TAG, String.format("%s removed from the complete list!", item.getTitle()));
                            }
                        }
                    }
        }));
    }

}
