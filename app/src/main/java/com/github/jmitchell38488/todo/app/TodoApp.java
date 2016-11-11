package com.github.jmitchell38488.todo.app;

import android.app.Application;
import android.content.Context;

import com.github.jmitchell38488.todo.app.data.DataModule;

public class TodoApp extends Application {

    private TodoAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerTodoAppComponent.builder()
                .todoModule(new TodoModule(this))
                .dataModule(new DataModule())
                .build();
    }

    public static TodoAppComponent getComponent(Context context) {
        return ((TodoApp) context.getApplicationContext()).component;
    }
}
