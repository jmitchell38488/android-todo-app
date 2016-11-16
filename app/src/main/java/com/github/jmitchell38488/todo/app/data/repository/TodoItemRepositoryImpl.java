package com.github.jmitchell38488.todo.app.data.repository;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.data.provider.meta.TodoItemMeta;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class TodoItemRepositoryImpl implements TodoItemRepository {

    private final ContentResolver mContentResolver;
    private final BriteContentResolver mBriteContentResolver;

    public TodoItemRepositoryImpl(ContentResolver contentResolver,
                                  BriteContentResolver briteContentResolver) {
        mContentResolver = contentResolver;
        mBriteContentResolver = briteContentResolver;
    }

    public TodoItemRepositoryImpl(BriteContentResolver briteContentResolver) {
        this(null, briteContentResolver);
    }

    @Override
    public Observable<List<TodoItem>> getAllItems() {
        return mBriteContentResolver.createQuery(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, null, null, TodoContract.TodoItem.DEFAULT_SORT, true)
                .map(TodoItemMeta.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TodoItem>> getAllCompletedItems() {
        String selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
        String[] args = {"1"};

        return mBriteContentResolver.createQuery(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, TodoContract.TodoItem.DEFAULT_SORT, true)
                .map(TodoItemMeta.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<TodoItem>> getAllNotCompletedItems() {

        String selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
        String[] args = {"0"};

        return mBriteContentResolver.createQuery(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, TodoContract.TodoItem.DEFAULT_SORT, true)
                .map(TodoItemMeta.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void saveTodoItem(TodoItem item) {
        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startInsert(-1, null, TodoContract.TodoItem.CONTENT_URI, new TodoItemMeta.Builder()
                .item(item)
                .build());
    }

    @Override
    public void deleteTodoItem(TodoItem item) {
        String where = TodoContract.TodoItem._ID + "=?";
        String[] args = {String.valueOf(item.getId())};

        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startDelete(-1, null, TodoContract.TodoItem.CONTENT_URI, where, args);
    }
}
