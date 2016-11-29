package com.github.jmitchell38488.todo.app.data.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract.TodoItemColumns;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract.TodoReminderColumns;
import com.github.jmitchell38488.todo.app.data.provider.meta.TodoItemMeta;

import java.util.List;

import javax.inject.Inject;

final public class TodoDatabase extends SQLiteOpenHelper {

    @Inject
    TodoStorage todoStorage;

    private static final String DB_NAME = "todos.db";
    private static final int DB_VERSION = 1;

    private final Context mContext;

    public interface Tables {
        String TODO_ITEMS = "items";
        String TODO_REMINDERS = "reminders";
    }

    public TodoDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        ((TodoApp) mContext.getApplicationContext()).getComponent(mContext).inject(this);

        db.execSQL("CREATE TABLE " + Tables.TODO_ITEMS + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoItemColumns.TODO_TITLE + " TEXT NOT NULL, " +
                TodoItemColumns.TODO_DESCRIPTION + " TEXT, " +
                TodoItemColumns.TODO_ORDER + " INTEGER NOT NULL DEFAULT 0, " +
                TodoItemColumns.TODO_PINNED + " INTEGER NOT NULL DEFAULT 0, " +
                TodoItemColumns.TODO_COMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                TodoItemColumns.TODO_LOCKED + " INTEGER NOT NULL DEFAULT 0);"
        );

        db.execSQL("CREATE TABLE " + Tables.TODO_REMINDERS + "(" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoReminderColumns.REMINDER_ITEM_ID + " INTEGER NOT NULL, " +
                TodoReminderColumns.REMINDER_YEAR + " INTEGER NOT NULL, " +
                TodoReminderColumns.REMINDER_MONTH + " INTEGER NOT NULL, " +
                TodoReminderColumns.REMINDER_DAY + " INTEGER NOT NULL, " +
                TodoReminderColumns.REMINDER_HOUR + " INTEGER NOT NULL DEFAULT 0, " +
                TodoReminderColumns.REMINDER_MINUTE + " INTEGER NOT NULL DEFAULT 0, " +
                TodoReminderColumns.REMINDER_ACTIVE + " INTEGER NOT NULL DEFAULT 0," +
                TodoReminderColumns.REMINDER_TIMES_SNOOZED + " INTEGER NOT NULL DEFAULT 0," +
                " FOREIGN KEY (" + TodoReminderColumns.REMINDER_ITEM_ID + ") REFERENCES " +
                Tables.TODO_ITEMS + " (" + BaseColumns._ID + "));"
        );

        // No todos, just add default ones
        if (todoStorage.getTodos().isEmpty()) {
            insertTodos(db);

        // If there are todos, convert them
        } else {
            convertTodos(db);
        }
    }

    private void convertTodos(SQLiteDatabase db) {
        List<TodoItem> todoItemList = todoStorage.getTodos();

        int orderPinned = 0;
        int orderCompleted = 0;
        int orderOther = 0;

        for (TodoItem item : todoItemList) {
            if (item.isPinned()) {
                orderPinned++;
                item.setOrder(orderPinned);
            } else if (item.isCompleted()) {
                orderCompleted++;
                item.setOrder(orderCompleted);
            } else {
                orderOther++;
                item.setOrder(orderOther);
            }

            ContentValues values = new TodoItemMeta.Builder().item(item).build();

            // Always remove ID or we will get a clash
            values.remove(TodoContract.TodoItem._ID);

            // Add record
            db.insert(Tables.TODO_ITEMS, null, values);
        }
    }

    private void insertTodos(SQLiteDatabase db) {
        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(1).order(1).completed(false).pinned(true).locked(false)
                .title("Pick up Airplane tickets")
                .description("This is a high priority item that will always appear at the top of the" +
                        "list. You can reorder high priority items among other high priority items, but" +
                        "not with other items.")
                .build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(2).order(1).completed(false).pinned(false).locked(false)
                .title("Pick up milk at the market")
                .description("This is a standard to do item that will appear beneath high priority" +
                        "items and above completed items. For all items, swiping left will mark" +
                        "them as complete, or active if already completed, and swiping left will" +
                        "permanently remove them. You can undo each action if you have undo enabled" +
                        "(it is by default).")
                .build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(3).order(2).completed(false).pinned(false).locked(true)
                .title("Buy front door lock")
                .description("This is an item that is locked and cannot be deleted." +
                        "Locked items can be important items, standard items, or completed items.")
                .build());

        db.insert(Tables.TODO_ITEMS, null, new TodoItemMeta.Builder()
                .id(4).order(1).completed(true).pinned(false).locked(false)
                .title("Buy movie tickets")
                .description("This is a completed to do item. You can make me active again by " +
                        "swiping to the left. Since I am complete, I will always be greyed out with" +
                        "strikethrough text, and will always appear at the bottom of the list.")
                .build());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing yet
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }

}
