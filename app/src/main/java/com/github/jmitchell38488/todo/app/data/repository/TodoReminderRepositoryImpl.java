package com.github.jmitchell38488.todo.app.data.repository;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.data.provider.meta.TodoReminderMeta;

import java.util.List;

public class TodoReminderRepositoryImpl implements TodoReminderRepository {

    private static final String LOG_TAG = TodoReminderRepositoryImpl.class.getSimpleName();

    private final ContentResolver mContentResolver;

    public TodoReminderRepositoryImpl(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    @Override
    public List<TodoReminder> getAllByTodoItemId(long itemId) {
        Cursor cursor = mContentResolver.query(
                TodoContract.TodoReminder.buildTodoItemUri(Long.toString(itemId)),
                TodoReminderMeta.PROJECTION,
                null,
                null,
                TodoContract.TodoReminder.DEFAULT_SORT
        );

        List<TodoReminder> items = TodoReminderMeta.PROJECTION_MAP.call(cursor);
        return items;
    }

    @Override
    public TodoReminder getReminderById(long id) {
        Cursor cursor = mContentResolver.query(
                TodoContract.TodoReminder.buildTodoReminderUri(Long.toString(id)),
                TodoReminderMeta.PROJECTION,
                TodoContract.TodoReminder._ID + "=?",
                new String[] {Long.toString(id)},
                TodoContract.TodoItem.DEFAULT_SORT
        );

        List<TodoReminder> items = TodoReminderMeta.PROJECTION_MAP.call(cursor);
        return !items.isEmpty() ? items.get(0) : null;
    }

    @Override
    public void saveTodoReminder(TodoReminder todoReminder) {
        ContentValues values = new TodoReminderMeta.Builder().reminder(todoReminder).build();

        // Make sure that we strip the ID off
        if (values.containsKey(TodoContract.TodoReminder._ID)) {
            values.remove(TodoContract.TodoReminder._ID);
        }

        if (todoReminder.getId() > 0) {
            String selection = TodoContract.TodoReminder._ID + "=?";
            String[] args = {Long.toString(todoReminder.getId())};

            AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
            handler.startUpdate(-1, null, TodoContract.TodoReminder.CONTENT_URI, values, selection, args);

            // Insert
        } else {
            AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    if (uri == null) {
                        return;
                    }

                    String str = TodoContract.TodoReminder.getTodoReminderId(uri);
                    long id = Long.parseLong(str);
                    todoReminder.setId(id);
                    Log.d(LOG_TAG, String.format("Setting reminder item id %d after insert", id));
                }
            };

            handler.startInsert(1, null, TodoContract.TodoReminder.CONTENT_URI, values);
        }
    }

    @Override
    public void deleteTodoReminder(TodoReminder todoReminder) {
        String where = TodoContract.TodoReminder._ID + "=?";
        String[] args = {String.valueOf(todoReminder.getId())};

        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startDelete(-1, null, TodoContract.TodoReminder.CONTENT_URI, where, args);
    }

}
