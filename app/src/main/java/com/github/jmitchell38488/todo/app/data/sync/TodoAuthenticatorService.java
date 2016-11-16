package com.github.jmitchell38488.todo.app.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TodoAuthenticatorService extends Service {

    private TodoAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new TodoAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
