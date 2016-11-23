package com.github.jmitchell38488.todo.app.data.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.util.DbUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

public interface TodoReminderMeta {

    String[] PROJECTION = {
            TodoContract.TodoReminder._ID,
            TodoContract.TodoReminder.REMINDER_ITEM_ID,
            TodoContract.TodoReminder.REMINDER_YEAR,
            TodoContract.TodoReminder.REMINDER_MONTH,
            TodoContract.TodoReminder.REMINDER_DAY,
            TodoContract.TodoReminder.REMINDER_HOUR,
            TodoContract.TodoReminder.REMINDER_MINUTE,
            TodoContract.TodoReminder.REMINDER_ACTIVE
    };

    Func1<Cursor, List<TodoReminder>> PROJECTION_MAP = cursor-> {
        try {
            List<TodoReminder> values = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                values.add(new TodoReminder()
                        .setId(DbUtils.getLong(cursor, TodoContract.TodoReminder._ID))
                        .setItemId(DbUtils.getLong(cursor, TodoContract.TodoReminder.REMINDER_ITEM_ID))
                        .setYear(DbUtils.getInt(cursor, TodoContract.TodoReminder.REMINDER_YEAR))
                        .setMonth(DbUtils.getInt(cursor, TodoContract.TodoReminder.REMINDER_MONTH))
                        .setDay(DbUtils.getInt(cursor, TodoContract.TodoReminder.REMINDER_DAY))
                        .setHour(DbUtils.getInt(cursor, TodoContract.TodoReminder.REMINDER_HOUR))
                        .setMinute(DbUtils.getInt(cursor, TodoContract.TodoReminder.REMINDER_MINUTE))
                        .setActive(DbUtils.getBoolean(cursor, TodoContract.TodoReminder.REMINDER_ACTIVE))
                );
            }

            return values;
        } finally {
            cursor.close();
        }
    };

    String[] ID_PROJECTION = {
            TodoContract.TodoReminder._ID
    };

    Func1<Cursor, Set<Long>> ID_PROJECTION_MAP = cursor -> {
        try {
            Set<Long> idSet = new HashSet<>(cursor.getCount());

            while (cursor.moveToNext()) {
                idSet.add(DbUtils.getLong(cursor, TodoContract.TodoReminder._ID));
            }

            return idSet;
        } finally {
            cursor.close();
        }
    };

    final class Builder {

        private final ContentValues values = new ContentValues();

        public TodoReminderMeta.Builder id(long id) {
            values.put(TodoContract.TodoReminder._ID, id);
            return this;
        }

        public TodoReminderMeta.Builder itemId(long itemId) {
            values.put(TodoContract.TodoReminder.REMINDER_ITEM_ID, itemId);
            return this;
        }

        public TodoReminderMeta.Builder year(int year) {
            values.put(TodoContract.TodoReminder.REMINDER_YEAR, year);
            return this;
        }

        public TodoReminderMeta.Builder month(int month) {
            values.put(TodoContract.TodoReminder.REMINDER_MONTH, month);
            return this;
        }

        public TodoReminderMeta.Builder day(int day) {
            values.put(TodoContract.TodoReminder.REMINDER_DAY, day);
            return this;
        }

        public TodoReminderMeta.Builder hour(int hour) {
            values.put(TodoContract.TodoReminder.REMINDER_HOUR, hour);
            return this;
        }

        public TodoReminderMeta.Builder minute(int minute) {
            values.put(TodoContract.TodoReminder.REMINDER_MINUTE, minute);
            return this;
        }

        public TodoReminderMeta.Builder active(boolean active) {
            values.put(TodoContract.TodoReminder.REMINDER_ACTIVE, active);
            return this;
        }

        public Builder reminder(TodoReminder reminder) {
            return id(reminder.getId())
                    .itemId(reminder.getItemId())
                    .year(reminder.getYear())
                    .month(reminder.getMonth())
                    .day(reminder.getDay())
                    .hour(reminder.getHour())
                    .minute(reminder.getMinute())
                    .active(reminder.isActive());
        }

        public ContentValues build() {
            return values;
        }

    }

}
