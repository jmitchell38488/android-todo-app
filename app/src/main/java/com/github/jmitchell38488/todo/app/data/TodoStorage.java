package com.github.jmitchell38488.todo.app.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerApp
public class TodoStorage {

    public static final String KEY_TODOS = "todos";

    final Gson gson;
    final SharedPreferences prefs;

    @Inject
    public TodoStorage(Gson gson, SharedPreferences prefs) {
        this.gson = gson;
        this.prefs = prefs;
    }

    public @NonNull List<TodoItem> getTodos() {
        List<TodoItem> list = new ArrayList<>();
        String json = prefs.getString(KEY_TODOS, "[]");

        try {
            TodoItem[] items = gson.fromJson(json, TodoItem[].class);
            for (TodoItem item : items) {
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            return list;
        }
    }
    public void saveTodos(@NonNull List<TodoItem> items) {
        prefs.edit()
                .putString(KEY_TODOS, gson.toJson(items.toArray()))
                .apply();
    }

    @Nullable
    public TodoItem popList() {
        List<TodoItem> items = getTodos();
        if (items.size() > 1) {
            TodoItem pop = items.remove(0);
            saveTodos(items);
            return pop;
        } else {
            saveTodos(new ArrayList<TodoItem>());
            return null;
        }
    }
}
