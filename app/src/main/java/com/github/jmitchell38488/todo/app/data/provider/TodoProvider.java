package com.github.jmitchell38488.todo.app.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.jmitchell38488.todo.app.util.SelectionBuilder;

import java.util.Arrays;

public class TodoProvider extends ContentProvider {

    private static final String LOG_TAG = TodoProvider.class.getSimpleName();

    private SQLiteOpenHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int TODOITEMS = 100;
    private static final int TODOITEM_ID = 101;
    private static final int TODOITEM_PAGE = 102;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TodoContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "todo", TODOITEMS);
        matcher.addURI(authority, "todo/page/*", TODOITEM_PAGE);
        matcher.addURI(authority, "todo/*", TODOITEM_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TodoDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        TodoDatabase.deleteDatabase(context);
        mOpenHelper = new TodoDatabase(getContext());
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOITEMS:
                return TodoContract.TodoItem.CONTENT_TYPE;
            case TODOITEM_PAGE:
                return TodoContract.TodoItem.CONTENT_TYPE;
            case TODOITEM_ID:
                return TodoContract.TodoItem.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        final SelectionBuilder builder = buildExpandedSelection(uri, match);

        Cursor cursor = builder
                .where(selection, selectionArgs)
                .query(db, false, projection, sortOrder, null);

        Context context = getContext();

        if (null != context) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODOITEMS:
                db.insertOrThrow(TodoDatabase.Tables.TODO_ITEMS, null, values);
                notifyChange(uri);
                return TodoContract.TodoItem.buildTodoItemUri(values.getAsString(TodoContract.TodoItem._ID));

            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);

        return this.buildExpandedSelection(uri, match);
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            // get all items
            case TODOITEMS:
            case TODOITEM_PAGE:
                return builder.table(TodoDatabase.Tables.TODO_ITEMS);

            // get single item
            case TODOITEM_ID:
                final String itemId = TodoContract.TodoItem.getTodoItemId(uri);
                return builder.table(TodoDatabase.Tables.TODO_ITEMS)
                        .where(Qualified.TODOITEM_ID + "=?", itemId);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private interface Qualified {
        String TODOITEM_ID = TodoDatabase.Tables.TODO_ITEMS + "." + TodoContract.TodoItem._ID;
    }
}
