package com.github.jmitchell38488.todo.app.data.adapter;

import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.adapter.TodoAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by justinmitchell on 13/11/2016.
 */

public class TodoItemSorter {

    private final static Object mLock = new Object();

    private final static Comparator<TodoItem> pinnedComparator = new PinnedComparator();
    private final static Comparator<TodoItem> completedComparator = new CompletedComparator();

    public static void sort(List<TodoItem> list) {
        // Don't sort if the lists are empty
        if (list.size() == 0) {
            return;
        }

        synchronized (mLock) {
            // Sort by pinned, so that Pinned are at the top
            Collections.sort(list, pinnedComparator);

            // Sort by completed so that Completed are at the bottom
            Collections.sort(list, completedComparator);
        }
    }

    public static void sortAdapter(TodoAdapter adapter) {
        // Don't sort if the adapter has no items
        if (adapter.getCount() == 0) {
            return;
        }

        synchronized (mLock) {
            // Don't notify on change yet
            adapter.setNotifyOnChange(false);

            // Sort by pinned, so that Pinned are at the top
            adapter.sort(pinnedComparator);

            // Sort by completed so that Completed are at the bottom
            adapter.sort(completedComparator);

            // Reinstate previous setting
            adapter.setNotifyOnChange(true);

            // Notify the listeners
            adapter.notifyDataSetChanged();
        }
    }

    private static class CompletedComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {
            return (lhs.isCompleted() == rhs.isCompleted() ? 0 : rhs.isCompleted() ? -1 : 1 );
        }

    }

    private static class PinnedComparator implements Comparator<TodoItem> {

        @Override
        public int compare(TodoItem lhs, TodoItem rhs) {
            return (lhs.isPinned() == rhs.isPinned() ? 0 : rhs.isPinned() ? 1 : -1);
        }

    }

}
