package com.github.jmitchell38488.todo.app.data.provider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

public class TodoContract {

    public interface TodoItemColumns {
        String TODO_TITLE = "item";
        String TODO_DESCRIPTION = "description";
        String TODO_ORDER = "item_order";
        String TODO_COMPLETED = "completed";
        String TODO_PINNED = "pinned";
        String TODO_LOCKED = "locked";
    }

    public interface TodoReminderColumns {
        String REMINDER_ITEM_ID = "item_id";
        String REMINDER_YEAR = "year";
        String REMINDER_MONTH = "month";
        String REMINDER_DAY = "day";
        String REMINDER_HOUR = "hour";
        String REMINDER_MINUTE = "minute";
        String REMINDER_ACTIVE = "active";
    }

    public static final String CONTENT_AUTHORITY = "com.github.jmitchell38488.todo.app.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODO = "todo";
    public static final String PATH_REMINDER = "reminder";

    public static class TodoItem implements TodoItemColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public static final String DEFAULT_SORT =
                TODO_PINNED + " DESC, " +
                TODO_COMPLETED + " ASC, " +
                TODO_ORDER + " ASC";

        public static Uri buildTodoItemsUri() {
            return CONTENT_URI;
        }

        public static Uri buildTodoItemUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static String getTodoItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class TodoReminder implements TodoReminderColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;

        public static final String DEFAULT_SORT = REMINDER_ITEM_ID + " ASC, " + REMINDER_ACTIVE + " DESC";

        public static Uri buildTodoReminderUri() {
            return CONTENT_URI;
        }

        public static Uri buildTodoReminderUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static Uri buildTodoItemUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(PATH_TODO).appendPath(itemId).build();
        }

        public static String getTodoReminderId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTodoItemId(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    private TodoContract() {
        throw new AssertionError("No instances.");
    }

}
