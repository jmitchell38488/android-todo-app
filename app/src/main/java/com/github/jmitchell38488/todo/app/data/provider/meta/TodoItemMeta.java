package com.github.jmitchell38488.todo.app.data.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.util.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

public interface TodoItemMeta {

    String[] PROJECTION = {
            TodoContract.TodoItem._ID,
            TodoContract.TodoItem.TODO_TITLE,
            TodoContract.TodoItem.TODO_DESCRIPTION,
            TodoContract.TodoItem.TODO_ORDER,
            TodoContract.TodoItem.TODO_PINNED,
            TodoContract.TodoItem.TODO_COMPLETED
    };

    Func1<SqlBrite.Query, List<TodoItem>> PROJECTION_MAP = query-> {
        Cursor cursor = query.run();
        try {
            List<TodoItem> values = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                values.add(new TodoItem()
                        .setId(DbUtils.getLong(cursor, TodoContract.TodoItem._ID))
                        .setTitle(DbUtils.getString(cursor, TodoContract.TodoItem.TODO_TITLE))
                        .setDescription(DbUtils.getString(cursor, TodoContract.TodoItem.TODO_DESCRIPTION))
                        .setOrder(DbUtils.getLong(cursor, TodoContract.TodoItem.TODO_ORDER))
                        .setPinned(DbUtils.getBoolean(cursor, TodoContract.TodoItem.TODO_PINNED))
                        .setCompleted(DbUtils.getBoolean(cursor, TodoContract.TodoItem.TODO_COMPLETED))
                );
            }

            return values;
        } finally {
            cursor.close();
        }
    };

    String[] ID_PROJECTION = {
            TodoContract.TodoItem._ID
    };

    Func1<SqlBrite.Query, Set<Long>> ID_PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
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

        public Builder item(TodoItem item) {
            return id(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .order(item.getOrder())
                    .pinned(item.isPinned())
                    .completed(item.isCompleted());
        }

        public ContentValues build() {
            return values;
        }

    }

}
