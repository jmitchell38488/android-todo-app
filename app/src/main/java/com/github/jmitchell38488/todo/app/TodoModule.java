package com.github.jmitchell38488.todo.app;

import android.app.Application;

import com.github.jmitchell38488.todo.app.annotation.PerApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TodoModule {

    final TodoApp app;

    public TodoModule(TodoApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    TodoApp provideTodoApp() {
        return app;
    }

    @Provides
    @Singleton
    Application provideApplication(TodoApp app) {
        return app;
    }

}
