package com.github.jmitchell38488.todo.app.util;

import com.github.jmitchell38488.todo.app.data.TodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinmitchell on 12/11/2016.
 */

public class ItemUtility {

    public static List<TodoItem> reorderTodoItemList(List<TodoItem> list) {
        List<TodoItem> incomplete = new ArrayList<>();
        List<TodoItem> complete = new ArrayList<>();

        for (TodoItem item : list) {
            if (item.isCompleted()) {
                complete.add(item);
            } else {
                incomplete.add(item);
            }
        }

        if (complete.size() > 0) {
            for (TodoItem item : complete) {
                incomplete.add(item);
            }
        }

        return incomplete;
    }

}
