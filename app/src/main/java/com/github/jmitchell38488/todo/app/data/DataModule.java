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

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    static final String PREFS_DEFAULT = "todo";

    public DataModule() {
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(PREFS_DEFAULT, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    TodoStorage provideTodoStorage(Gson gson, SharedPreferences sharedPreferences) {
        return new TodoStorage(gson, sharedPreferences);
    }

}
