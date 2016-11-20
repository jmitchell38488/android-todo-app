package com.github.jmitchell38488.todo.app.data.repository;

import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.jmitchell38488.todo.app.data.provider.TodoDatabase;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @Provides
    @Singleton
    public TodoItemRepository provideTodoItemsRepository(@Nullable ContentResolver contentResolver) {
        return new TodoItemRepositoryImpl(contentResolver);
    }

}
