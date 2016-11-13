package com.github.jmitchell38488.todo.app.data;

import java.util.Comparator;

/**
 * Created by justinmitchell on 13/11/2016.
 */

public class TodoItemSorter implements Comparator<TodoItem> {

    @Override
    public int compare(TodoItem lhs, TodoItem rhs) {
        return (lhs.isPinned() == rhs.isPinned()
                // Both pinned
                ? 0
                // Checking if both completed
                : ((lhs.isCompleted() || rhs.isCompleted())
                // Check completed
                    ?  (lhs.isCompleted() == rhs.isCompleted() ? 0 : rhs.isCompleted() ? -1 : 1 )
                // Check pinned
                    : (rhs.isPinned() ? 1 : -1))
        );
    }

}
