package com.github.jmitchell38488.todo.app.data.repository;

import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @Provides @PerApp
    public TodoItemRepository providesGenresRepository(BriteContentResolver contentResolver) {
        return new TodoItemRepositoryImpl(contentResolver);
    }

}
