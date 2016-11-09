package com.github.jmitchell38488.todo.app.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.google.gson.Gson;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    static final String PREFS_DEFAULT = "todo";

    public DataModule() {
    }

    @Provides @PerApp
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(PREFS_DEFAULT, Context.MODE_PRIVATE);
    }

    @Provides @PerApp
    Gson provideGson() {
        return new Gson();
    }

    @Provides @PerApp
    TodoAdapter provideTodoAdapter(Application app) {
        ArrayList<TodoItem> items = new ArrayList<>();
        items.add(new TodoItem("Foo"));
        items.add(new TodoItem("Bar"));
        items.add(new TodoItem("Sleep"));
        items.add(new TodoItem("Get up"));
        items.add(new TodoItem("Go to work"));
        items.add(new TodoItem("Do Android Work! :D"));

        return new TodoAdapter(app.getApplicationContext(), items);
    }
}
