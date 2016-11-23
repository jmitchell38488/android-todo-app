package com.github.jmitchell38488.todo.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;

import com.github.jmitchell38488.todo.app.R;

public class PreferencesUtility {

    public static int getTimesRun(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int defValue = Integer.parseInt(context.getString(R.string.pref_times_run_default));
        return prefs.getInt(context.getString(R.string.pref_times_run_key), defValue);
    }

    public static void incrementTimesRun(Context context) {
        int timesRun = getTimesRun(context);
        timesRun++;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putInt(context.getString(R.string.pref_times_run_key), timesRun);
        spe.commit();
    }

    public static long getInstallTime(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long defValue = Integer.parseInt(context.getString(R.string.pref_install_time_default));
        return prefs.getLong(context.getString(R.string.pref_install_time_key), defValue);
    }

    public static void setInstallTime(Context context) {
        long installTime = getInstallTime(context);

        // Don't update it if it's already set!
        if (installTime > 0) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putLong(context.getString(R.string.pref_install_time_key), System.currentTimeMillis());
        spe.commit();
    }

    public static boolean userEnabledPeriodicNotifications(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean defValue = Boolean.parseBoolean(context.getString(R.string.pref_periodic_notification_enabled_default));
        return prefs.getBoolean(context.getString(R.string.pref_periodic_notification_enabled_key), defValue);
    }

    public static void setUserEnabledPeriodicNotifications(final Context context, boolean state) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putBoolean(context.getString(R.string.pref_periodic_notification_enabled_key), state);
        spe.commit();
    }

    public static boolean isPeriodicNotificationsActive(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean defValue = Boolean.parseBoolean(context.getString(R.string.pref_periodic_notification_active_default));
        return prefs.getBoolean(context.getString(R.string.pref_periodic_notification_active_key), defValue);
    }

    public static void setPeriodicNotificationsActive(final Context context, boolean state) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putBoolean(context.getString(R.string.pref_periodic_notification_active_key), state);
        spe.commit();
    }

    public static int getAlarmSnoozeTime(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int defValue = Integer.parseInt(context.getString(R.string.pref_alarm_snooze_time_default));
        return prefs.getInt(context.getString(R.string.pref_alarm_snooze_time_key), defValue);
    }

    public static void setAlarmSnoozeTime(final Context context, int snoozeTime) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putInt(context.getString(R.string.pref_alarm_snooze_time_key), snoozeTime);
        spe.commit();
    }

    public static int getMaxAlarmSnoozeTimes(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int defValue = Integer.parseInt(context.getString(R.string.pref_alarm_snooze_maxtimes_default));
        return prefs.getInt(context.getString(R.string.pref_alarm_snooze_maxtimes_key), defValue);
    }

    public static void setMaxAlarmSnoozeTimes(final Context context, int times) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putInt(context.getString(R.string.pref_alarm_snooze_maxtimes_key), times);
        spe.commit();
    }

    public static long getMaxAllowedTimeBeforeAutoSnooze(final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int defValue = Integer.parseInt(context.getString(R.string.pref_alarm_snooze_timeout_default));
        return prefs.getInt(context.getString(R.string.pref_alarm_snooze_timeout_key), defValue);
    }

    public static void setMaxAllowedTimeBeforeAutoSnooze(final Context context, int time) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putInt(context.getString(R.string.pref_alarm_snooze_timeout_key), time);
        spe.commit();
    }

}
