package com.github.jmitchell38488.todo.app;

import android.app.Application;

import com.github.jmitchell38488.todo.app.data.provider.ProviderModule;
import com.github.jmitchell38488.todo.app.data.provider.TodoDatabase;
import com.github.jmitchell38488.todo.app.data.repository.RepositoryModule;
import com.github.jmitchell38488.todo.app.data.service.AutoStartReceiver;
import com.github.jmitchell38488.todo.app.data.service.PeriodicNotificationService;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarmButtonReceiver;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarmService;
import com.github.jmitchell38488.todo.app.data.service.ServiceModule;
import com.github.jmitchell38488.todo.app.ui.activity.EditDescriptionActivity;
import com.github.jmitchell38488.todo.app.ui.activity.EditItemActivity;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.data.DataModule;
import com.github.jmitchell38488.todo.app.ui.activity.TriggeredAlarmActivity;
import com.github.jmitchell38488.todo.app.ui.fragment.BaseFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.SortedListFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.TriggeredAlarmFragment;
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
                TodoItemModule.class,
                ServiceModule.class
        }
)
public interface TodoAppComponent {

    void inject(ListActivity listActivity);

    void inject(SortedListFragment sortedListFragment);

    void inject(ListFragment listFragment);

    void inject(BaseFragment baseFragment);

    void inject(PeriodicNotificationService periodicNotificationService);

    void inject(AutoStartReceiver autoStartReceiver);

    void inject(ReminderAlarmService reminderAlarmService);

    void inject(TriggeredAlarmActivity triggeredAlarmActivity);

    void inject(TriggeredAlarmFragment triggeredAlarmFragment);

    void inject(EditItemActivity editItemActivity);

    void inject(TodoDatabase todoDatabase);

    void inject(ReminderAlarmButtonReceiver reminderAlarmButtonReceiver);

    void inject(EditDescriptionActivity editDescriptionActivity);

    Application application();

    Gson gson();

}
