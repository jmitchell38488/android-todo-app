package com.github.jmitchell38488.todo.app.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by justinmitchell on 16/11/2016.
 */

public class TodoSyncService extends Service {

    private static final Object LOCK = new Object();
    private static TodoSyncAdapter mSyncAdapter;

    @Override
    public void onCreate() {
        synchronized (LOCK) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new TodoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }

}
