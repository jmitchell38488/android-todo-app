package com.github.jmitchell38488.todo.app.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        items.add(new TodoItem("Foo", "Getting my Foo on", 1, false));
        items.add(new TodoItem("Bar", "Getting my Bar on", 1, false));
        items.add(new TodoItem("Sleep", "Gotta catch up on my sleepless nights", 1, false));
        items.add(new TodoItem("Get up", "Gotta pull my butt outta bed, no more sleep!", 1, false));
        items.add(new TodoItem("Go to work", "Off to work I go, gotta get that bus!", 1, false));
        items.add(new TodoItem("Do Android Work! :D", "Gotta learn to get that job!", 1, false));
        items.add(new TodoItem("Interview with NAB", "Being awesome, is awesome.", 1, false));
        items.add(new TodoItem("Be awesome at something", "Being awesome, is awesome.", 1, false));
        items.add(new TodoItem("Get wet and wild on the job!", "Have fun!", 1, false));

        return new TodoAdapter(app.getApplicationContext(), items);
    }

}
