package com.github.jmitchell38488.todo.app.data.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TodoContract {

    public interface TodoItemColumns {
        String TODO_TITLE = "item";
        String TODO_DESCRIPTION = "description";
        String TODO_ORDER = "order";
        String TODO_COMPLETED = "completed";
        String TODO_PINNED = "pinned";
    }

    public static final String CONTENT_AUTHORITY = "com.github.jmitchell38488.todo.app.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_TODO = "todo";

    public static class TodoItem implements TodoItemColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public static final String DEFAULT_SORT = TODO_PINNED + " DESC, " +
                TODO_COMPLETED + " ASC" + TODO_ORDER + " ASC";

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

    private TodoContract() {
        throw new AssertionError("No instances.");
    }

}
