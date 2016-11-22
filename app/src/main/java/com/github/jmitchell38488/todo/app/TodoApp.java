package com.github.jmitchell38488.todo.app;

import android.app.Application;
import android.content.Context;

import com.github.jmitchell38488.todo.app.data.DataModule;
import com.github.jmitchell38488.todo.app.data.provider.ProviderModule;
import com.github.jmitchell38488.todo.app.data.repository.RepositoryModule;
import com.github.jmitchell38488.todo.app.data.service.ServiceModule;
import com.github.jmitchell38488.todo.app.ui.module.TodoItemModule;

public class TodoApp extends Application {

    private TodoAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerTodoAppComponent.builder()
                .todoModule(new TodoModule(this))
                .dataModule(new DataModule())
                .providerModule(new ProviderModule())
                .repositoryModule(new RepositoryModule())
                .todoItemModule(new TodoItemModule())
                .serviceModule(new ServiceModule(this))
                .build();
    }

    public static TodoAppComponent getComponent(Context context) {
        return ((TodoApp) context.getApplicationContext()).component;
    }
}
