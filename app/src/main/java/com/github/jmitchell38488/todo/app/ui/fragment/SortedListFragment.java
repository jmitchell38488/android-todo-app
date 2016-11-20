package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;
import com.github.jmitchell38488.todo.app.ui.adapter.Pager;
import com.github.jmitchell38488.todo.app.ui.listener.EndlessScrollListener;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class SortedListFragment extends ListFragment implements EndlessScrollListener.OnLoadMoreCallback {

    private static final String LOG_TAG = SortedListFragment.class.getSimpleName();
    private static final String ARG_SORT = "state_sort";
    private static final String ARG_FILTER = "state_filter";
    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private static final String STATE_IS_LOADING = "state_is_loading";

    private EndlessScrollListener mEndlessScrollListener;
    private static final BehaviorSubject<Observable<List<TodoItem>>> mItemsObservableSubject = BehaviorSubject.create();
    private Pager<List<TodoItem>, List<TodoItem>> mPager;

    private Sort mSort;
    private Filter mFilter;
    private int mCurrentPage = 0;
    private boolean mIsLoading = false;
    private boolean mPagerInitialized = false;

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
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE, 0);
            mIsLoading = savedInstanceState.getBoolean(STATE_IS_LOADING, true);
        }

        mAdapter.setLoadMore(true);
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

        //subscribeToItems();
        if (savedInstanceState == null) {
            reloadContent();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        outState.putBoolean(STATE_IS_LOADING, mIsLoading);
        outState.putSerializable(ARG_SORT, mSort);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (mAdapter.isLoadMore()) {
            pullPage(page);
        }
    }

    protected final void reloadContent() {
        mCurrentPage = -1;
        reAddOnScrollListener(mLayoutManager, mCurrentPage = 0);
        mAdapter.clear();
        pullPage(1);
    }

    protected boolean noMorePages() {
        return false;
    }

    private void subscribeToItems() {
        Observable<List<TodoItem>> source = mItemRepository.getItems(mCurrentPage > 0 ? mCurrentPage : 1, mSort, mFilter);
        mPager = Pager.create(list -> {
            return !mPagerInitialized ? null : mItemRepository.getItems(mCurrentPage + 1, mSort, mFilter);
        });

        mPager.page(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    if (items == null) {
                        return;
                    }

                    mCurrentPage++;

                    if (mCurrentPage == 1) {
                        mAdapter.clear();
                    }

                    mAdapter.add(items);
                    return;
                });

        mPagerInitialized = true;
    }

    private void pullPage(int page) {
        Log.d(LOG_TAG, String.format("Loading page %d", page));

        if (mPager == null) {
            subscribeToItems();
            mCurrentPage = 0;
        } else {
            mPager.next();
        }
    }


    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        reAddOnScrollListener(mLayoutManager, mCurrentPage);
    }

    private void reAddOnScrollListener(LinearLayoutManager layoutManager, int startPage) {
        if (mEndlessScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
        }

        mEndlessScrollListener = EndlessScrollListener
                .fromLinearLayoutManager(layoutManager, 5, startPage)
                .setCallback(this);

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
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
