package com.github.jmitchell38488.todo.app.data.repository;

import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class RepositoryModule {

    @Singleton
    @Provides
    public TodoItemRepository providesGenresRepository(BriteContentResolver contentResolver) {
        return new TodoItemRepositoryImpl(contentResolver);
    }

}
