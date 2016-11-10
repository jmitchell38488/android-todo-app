package com.github.jmitchell38488.todo.app.util;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

import com.github.jmitchell38488.todo.app.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by justinmitchell on 10/11/2016.
 */

public class DateUtility {

    public static long getMidnightUTCTimeToday() {
        Calendar date = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);

        return date.getTimeInMillis();
    }

    /**
     * Helper method to convert a given timestamp in the format of yyyy-MM-dd kk:mm:ssZ (eg.
     * 2016-04-04 09:12:34-0800) into a unit timestamp with milliseconds
     *
     * @param context Context to use for resource localization
     * @param dateTime Timestamp in required format
     * @return
     */
    public static long convertDateStringToLong(Context context, String dateTime) {
        if (TextUtils.isEmpty(dateTime)) {
            return 0l;
        }

        try {
            DateFormat df = new SimpleDateFormat(context.getString(R.string.date_format), Locale.ENGLISH);
            Date date = df.parse(dateTime);
            return date.getTime();
        } catch (ParseException e) {
            //
        }

        return 0l;
    }

    /**
     * Helper method to convert the database representation of the dateTime into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon Jun 8"
     *
     * @param context Context to use for resource localization
     * @return a user-friendly representation of the dateTime.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);

        // If the dateTime we're building the String for is today's dateTime, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(
                    context.getString(formatId),
                    today,
                    getFormattedMonthDay(context, dateInMillis)
            );
        } else if ( julianDay < currentJulianDay + 7 ) {
            // If the input dateTime is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat(context.getString(R.string.date_format_daymonthday));
            return shortenedDateFormat.format(dateInMillis);
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);

        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday")
            SimpleDateFormat dayFormat = new SimpleDateFormat(context.getString(R.string.date_format_dayname));
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db dateTime format to the format "Month day", e.g "June 24".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis) {
        Time time = new Time();
        time.setToNow();

        //SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat(context.getString(R.string.date_format_monthday));
        String monthDayString = monthDayFormat.format(dateInMillis);

        return monthDayString;
    }

}
