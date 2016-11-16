package com.github.jmitchell38488.todo.app.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract.TodoItemColumns;
import com.github.jmitchell38488.todo.app.data.provider.meta.TodoItemMeta;


final public class TodoDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "todos.db";
    private static final int DB_VERSION = 1;

    private final Context mContext;

    interface Tables {
        String TODO_ITEMS = "items";
    }

    public TodoDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.TODO_ITEMS + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoItemColumns.TODO_TITLE + " TEXT NOT NULL, " +
                TodoItemColumns.TODO_DESCRIPTION + " TEXT, " +
                TodoItemColumns.TODO_ORDER + " INTEGER NOT NULL DEFAULT 0, " +
                TodoItemColumns.TODO_PINNED + " INTEGER NOT NULL DEFAULT 0, " +
                TodoItemColumns.TODO_COMPLETED + " INTEGER NOT NULL DEFAULT 0"
        );

        insertTodos(db);
    }

    /**
     * Inserts predefined list of movie genres taken from Movie Database.
     * Ideally we should fetch them from api on first launch.
     */
    private void insertTodos(SQLiteDatabase db) {
        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).title("Test To Do").description("This is a test")
                .order(1).completed(false).pinned(false).build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).title("Test pinned").description("This is a pinned to do, it should be at the top")
                .order(1).completed(false).pinned(true).build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).title("Test complete").description("This is a completed to to, it should be at the bottom")
                .order(1).completed(true).pinned(false).build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).title("Test foo").description("This should be 2nd")
                .order(1).completed(false).pinned(false).build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).title("Test bar").description("This should be 3rd")
                .order(1).completed(false).pinned(false).build());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing yet
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }

}