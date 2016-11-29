package com.github.jmitchell38488.todo.app.util;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class AlarmUtility {

    public static Map<Uri, String> getAlarmSounds(Context context) {
        Map<Uri, String> map = new HashMap<>();

        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();

        while (cursor.moveToNext()) {
            String name = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String uriIndex = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            Uri uri = Uri.parse(uriIndex + "/" + id);

            map.put(uri, name);
        }

        return map;
    }
}
