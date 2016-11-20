package com.github.jmitchell38488.todo.app.data.repository;

import android.content.ContentResolver;

import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @Provides
    @Singleton
    public TodoItemRepository provideTodoItemsRepository(ContentResolver contentResolver, BriteContentResolver briteContentResolver) {
        return new TodoItemRepositoryImpl(contentResolver, briteContentResolver);
    }

}
