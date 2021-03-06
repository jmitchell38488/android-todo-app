package com.github.jmitchell38488.todo.app.data.repository;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.data.provider.meta.TodoItemMeta;

import java.util.List;

public class TodoItemRepositoryImpl implements TodoItemRepository {

    private static final String LOG_TAG = TodoItemRepositoryImpl.class.getSimpleName();

    private final ContentResolver mContentResolver;

    public TodoItemRepositoryImpl(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    @Override
    public List<TodoItem> getAllItems() {
        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, null, null, TodoContract.TodoItem.DEFAULT_SORT);

        List<TodoItem> items = TodoItemMeta.PROJECTION_MAP.call(cursor);
        return items;
    }

    @Override
    public List<TodoItem> getAllItems(Sort sort, Filter filter) {
        String selection = "";
        String[] args = {};
        String tsort = "";

        if (sort == null) {
            sort = Sort.DEFAULT;
        }

        if (filter == null) {
            filter = Filter.DEFAULT;
        }

        switch (filter) {
            case COMPLETED:
                selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
                args = new String[]{"1"};
                break;

            case PINNED:
                selection = TodoContract.TodoItem.TODO_PINNED + "=?";
                args = new String[]{"1"};
                break;
        }

        switch (sort) {
            case DEFAULT:
                tsort = TodoContract.TodoItem.DEFAULT_SORT;
                break;

            case COMPLETED:
                tsort = TodoContract.TodoItem.TODO_COMPLETED + " ASC";
                break;

            case PINNED:
                tsort = TodoContract.TodoItem.TODO_PINNED + " ASC";
                break;
        }

        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, tsort);

        List<TodoItem> items = TodoItemMeta.PROJECTION_MAP.call(cursor);
        return items;
    }

    @Override
    public List<TodoItem> getAllCompletedItems() {
        String selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
        String[] args = {"1"};

        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, TodoContract.TodoItem.DEFAULT_SORT);

        List<TodoItem> items = TodoItemMeta.PROJECTION_MAP.call(cursor);
        return items;
    }

    @Override
    public List<TodoItem> getAllNotCompletedItems() {
        String selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
        String[] args = {"0"};

        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, TodoContract.TodoItem.DEFAULT_SORT);

        List<TodoItem> items = TodoItemMeta.PROJECTION_MAP.call(cursor);
        return items;
    }

    @Override
    public TodoItem getItem(int id) {
        String selection = TodoContract.TodoItem._ID + "=?";
        String[] args = {Integer.toString(id)};

        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                TodoItemMeta.PROJECTION, selection, args, TodoContract.TodoItem.DEFAULT_SORT);

        List<TodoItem> items = TodoItemMeta.PROJECTION_MAP.call(cursor);

        if (items.isEmpty()) {
            return null;
        }

        return items.get(0);
    }

    @Override
    public int getItemCount(Filter filter) {
        String selection = "";
        String[] args = {};

        if (filter == null) {
            filter = Filter.DEFAULT;
        }

        switch (filter) {
            case COMPLETED:
                selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
                args = new String[]{"1"};
                break;

            case PINNED:
                selection = TodoContract.TodoItem.TODO_PINNED + "=?";
                args = new String[]{"1"};
                break;

            case DEFAULT:
                selection = TodoContract.TodoItem.TODO_COMPLETED + "=?";
                args = new String[]{"0"};
                break;
        }

        Cursor cursor = mContentResolver.query(TodoContract.TodoItem.CONTENT_URI,
                new String[] {"count(*) as count"},
                selection,
                args,
                null);

        cursor.moveToFirst();
        int rowCount = cursor.getInt(0);

        return rowCount;
    }

    @Override
    public void saveTodoItem(TodoItem item) {
        ContentValues values = new TodoItemMeta.Builder().item(item).build();

        // Make sure that we strip the ID off
        if (values.containsKey(TodoContract.TodoItem._ID)) {
            values.remove(TodoContract.TodoItem._ID);
        }

        if (item.getId() > 0) {
            String selection = TodoContract.TodoItem._ID + "=?";
            String[] args = {Long.toString(item.getId())};

            AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
            handler.startUpdate(-1, null, TodoContract.TodoItem.CONTENT_URI, values, selection, args);

        // Insert
        } else {
            AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    if (uri == null) {
                        return;
                    }

                    String str = TodoContract.TodoItem.getTodoItemId(uri);
                    long id = Long.parseLong(str);
                    item.setId(id);
                    Log.d(LOG_TAG, String.format("Setting item id %d after insert", id));
                }
            };

            handler.startInsert(1, null, TodoContract.TodoItem.CONTENT_URI, values);
        }
    }

    @Override
    public void deleteTodoItem(TodoItem item) {
        String where = TodoContract.TodoItem._ID + "=?";
        String[] args = {String.valueOf(item.getId())};

        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startDelete(-1, null, TodoContract.TodoItem.CONTENT_URI, where, args);
    }

    @Override
    public void saveTodoItemList(List<TodoItem> list) {
        if (list.isEmpty()) {
            return;
        }

        for (TodoItem item : list) {
            saveTodoItem(item);
        }
    }
}
