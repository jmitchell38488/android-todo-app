package com.github.jmitchell38488.todo.app.util;

import com.github.jmitchell38488.todo.app.data.TodoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by justinmitchell on 12/11/2016.
 */

public class ItemUtility {

    public static void reorderTodoItemList(List<TodoItem> list) {
        Object mLock = false;
        synchronized (mLock) {
            Collections.sort(list, new Comparator<TodoItem>() {
                @Override
                public int compare(TodoItem lhs, TodoItem rhs) {
                    return (lhs.isCompleted() == rhs.isCompleted() ? 0 : rhs.isCompleted() ? -1 : 1);
                }
            });
        }
    }

}
