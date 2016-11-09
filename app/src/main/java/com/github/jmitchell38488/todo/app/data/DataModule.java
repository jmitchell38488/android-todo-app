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
        items.add(new TodoItem("Foo", "Getting my Foo on", "2016-10-30T09:00:00+11:00",
                        "2016-11-02T09:00:00+11:00", 1, false));

        items.add(new TodoItem("Bar", "Getting my Bar on", "2016-10-30T09:00:00+11:00",
                "2016-11-03T09:00:00+11:00", 1, false));

        items.add(new TodoItem("Sleep", "Gotta catch up on my sleepless nights",
                "2016-11-09T09:00:00+11:00", "2016-11-10T21:00:00+11:00", 1, false));

        items.add(new TodoItem("Get up", "Gotta pull my butt outta bed, no more sleep!",
                "2016-11-09T09:00:00+11:00", "2016-11-11T06:30:00+11:00", 1, false));

        items.add(new TodoItem("Go to work", "Off to work I go, gotta get that bus!",
                "2016-11-10T09:00:00+11:00", "2016-11-11T07:20:00+11:00", 1, false));

        items.add(new TodoItem("Do Android Work! :D", "Gotta learn to get that job!",
                "2016-11-10T09:00:00+11:00", "2016-11-11T09:30:00+11:00", 1, false));

        return new TodoAdapter(app.getApplicationContext(), items);
    }
}