package com.github.jmitchell38488.todo.app.data.provider;

import android.app.Application;
import android.content.ContentResolver;

import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by justinmitchell on 16/11/2016.
 */

@Module
public class ProviderModule {

    @Provides @PerApp
    SqlBrite provideSqlBrite() {
        return SqlBrite.create();
    }

    @Provides @PerApp
    ContentResolver provideContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides @PerApp
    BriteContentResolver provideBrideContentResolver(SqlBrite sqlBrite, ContentResolver contentResolver) {
        return sqlBrite.wrapContentProvider(contentResolver);
    }

}
