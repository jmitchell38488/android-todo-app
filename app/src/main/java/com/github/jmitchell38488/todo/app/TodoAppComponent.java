package com.github.jmitchell38488.todo.app;

import android.app.Application;

import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.data.DataModule;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.google.gson.Gson;

import dagger.Component;

@PerApp
@Component (
    modules = {
        TodoModule.class,
        DataModule.class
    }
)
public interface TodoAppComponent {

    public void inject(ListActivity listActivity);

    public void inject(ListFragment listFragment);

    Application application();

    Gson gson();

}
