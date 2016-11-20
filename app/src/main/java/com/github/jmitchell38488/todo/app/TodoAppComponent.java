package com.github.jmitchell38488.todo.app;

import android.app.Application;
import android.content.Context;

import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.github.jmitchell38488.todo.app.data.provider.ProviderModule;
import com.github.jmitchell38488.todo.app.data.repository.RepositoryModule;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.data.DataModule;
import com.github.jmitchell38488.todo.app.ui.fragment.BaseFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.SortedListFragment;
import com.github.jmitchell38488.todo.app.ui.module.TodoItemModule;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (
        modules = {
                TodoModule.class,
                DataModule.class,
                ProviderModule.class,
                RepositoryModule.class,
                TodoItemModule.class
        }
)
public interface TodoAppComponent {

    public void inject(ListActivity listActivity);

    public void inject(SortedListFragment sortedListFragment);

    public void inject(ListFragment listFragment);

    public void inject(BaseFragment baseFragment);

    Application application();

    Gson gson();

}
