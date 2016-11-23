package com.github.jmitchell38488.todo.app.util;

import android.provider.AlarmClock;

public class PreferencesUtility {

    public static boolean userEnabledPeriodicNotifications() {
        return true;
    }

    public static boolean isPeriodicNotificationsActive() {
        return true;
    }

    public static void setPeriodicNotificationsActive(boolean state) {
        //
    }

    public static int getAlarmSnoozeTime() {
        return 5;
    }

    public static int getMaxAlarmSnoozeTimes() {
        return 5;
    }

    public static long getMaxAllowedTimeBeforeAutoSnooze() {
        return 60 * 1000;
    }

}
