package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import rx.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.subjects.BehaviorSubject;

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
                            doCompleteAction(position);
                            break;
                        }
                    }
        }));
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

        Log.d(LOG_TAG, "doCompleteAction (" + item.getTitle() + "), " + item.isCompleted());
    }

}
