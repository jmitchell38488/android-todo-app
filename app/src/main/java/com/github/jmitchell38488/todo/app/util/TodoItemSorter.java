package com.github.jmitchell38488.todo.app.util;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

    public void reorderList(List<TodoItem> itemList) {
        // Clone the items, Map is <id, i>
        HashMap<Long, Integer> preSortMap = new HashMap<>();
        for (int i = 0; i < itemList.size(); i++) {
            preSortMap.put(itemList.get(i).getId(), i);
        }

        List<TodoItem> mItemsCopy = new ArrayList<>(itemList.size());
        Collections.copy(mItemsCopy, itemList);
        TodoItemSorter.sort(mItemsCopy);
    }

}
