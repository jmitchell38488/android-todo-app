package com.github.jmitchell38488.todo.app.data.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.data.provider.TodoDatabase;
import com.github.jmitchell38488.todo.app.util.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

public interface TodoItemMeta {

    String[] PROJECTION = {
            TodoDatabase.Tables.TODO_ITEMS + "." + TodoContract.TodoItem._ID + " as _id",
            TodoContract.TodoItem.TODO_TITLE,
            TodoContract.TodoItem.TODO_DESCRIPTION,
            TodoContract.TodoItem.TODO_ORDER,
            TodoContract.TodoItem.TODO_PINNED,
            TodoContract.TodoItem.TODO_COMPLETED,
            TodoContract.TodoItem.TODO_LOCKED,
            TodoDatabase.Tables.TODO_REMINDERS + "." + TodoContract.TodoReminder._ID + " as reminder_id"
    };

    Func1<Cursor, List<TodoItem>> PROJECTION_MAP = cursor-> {
        try {
            List<TodoItem> values = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                TodoItem item = new TodoItem()
                                .setId(DbUtils.getLong(cursor, TodoContract.TodoItem._ID))
                                .setTitle(DbUtils.getString(cursor, TodoContract.TodoItem.TODO_TITLE))
                                .setDescription(DbUtils.getString(cursor, TodoContract.TodoItem.TODO_DESCRIPTION))
                                .setOrder(DbUtils.getLong(cursor, TodoContract.TodoItem.TODO_ORDER))
                                .setPinned(DbUtils.getBoolean(cursor, TodoContract.TodoItem.TODO_PINNED))
                                .setCompleted(DbUtils.getBoolean(cursor, TodoContract.TodoItem.TODO_COMPLETED))
                                .setLocked(DbUtils.getBoolean(cursor, TodoContract.TodoItem.TODO_LOCKED));

                // This is transient only, we don't want to store this in the database
                item.hasReminder = DbUtils.getLong(cursor, "reminder_id") > 0;
                values.add(item);
            }

            return values;
        } finally {
            cursor.close();
        }
    };

    String[] ID_PROJECTION = {
            TodoContract.TodoItem._ID
    };

    Func1<Cursor, Set<Long>> ID_PROJECTION_MAP = cursor -> {
        try {
            Set<Long> idSet = new HashSet<>(cursor.getCount());

            while (cursor.moveToNext()) {
                idSet.add(DbUtils.getLong(cursor, TodoContract.TodoItem._ID));
            }

            return idSet;
        } finally {
            cursor.close();
        }
    };

    final class Builder {

        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(TodoContract.TodoItem._ID, id);
            return this;
        }

        public Builder title(String title) {
            values.put(TodoContract.TodoItem.TODO_TITLE, title);
            return this;
        }

        public Builder description(String description) {
            values.put(TodoContract.TodoItem.TODO_DESCRIPTION, description);
            return this;
        }

        public Builder order(long order) {
            values.put(TodoContract.TodoItem.TODO_ORDER, order);
            return this;
        }

        public Builder pinned(boolean pinned) {
            values.put(TodoContract.TodoItem.TODO_PINNED, pinned);
            return this;
        }

        public Builder completed(boolean completed) {
            values.put(TodoContract.TodoItem.TODO_COMPLETED, completed);
            return this;
        }

        public Builder locked(boolean locked) {
            values.put(TodoContract.TodoItem.TODO_LOCKED, locked);
            return this;
        }

        public Builder item(TodoItem item) {
            return id(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .order(item.getOrder())
                    .pinned(item.isPinned())
                    .completed(item.isCompleted())
                    .locked(item.isLocked());
        }

        public ContentValues build() {
            return values;
        }

    }

}
