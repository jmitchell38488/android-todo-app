package com.github.jmitchell38488.todo.app.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.jmitchell38488.todo.app.data.provider.meta.TodoReminderMeta;

public class TodoReminder implements Cloneable, Parcelable, TodoReminderMeta {

    long id;
    long itemId;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    boolean active;

    public TodoReminder() {
        active = false;
    }

    public TodoReminder(long id, long itemId, int year, int month, int day, int hour, int minute, boolean active) {
        this.id = id;
        this.itemId = itemId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public TodoReminder setId(long id) {
        this.id = id;
        return this;
    }

    public long getItemId() {
        return itemId;
    }

    public TodoReminder setItemId(long itemId) {
        this.itemId = itemId;
        return this;
    }

    public int getYear() {
        return year;
    }

    public TodoReminder setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMonth() {
        return month;
    }

    public TodoReminder setMonth(int month) {
        this.month = month;
        return this;
    }

    public int getDay() {
        return day;
    }

    public TodoReminder setDay(int day) {
        this.day = day;
        return this;
    }

    public int getHour() {
        return hour;
    }

    public TodoReminder setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public TodoReminder setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public TodoReminder setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public Object clone() {
        return new TodoReminder(id, itemId, year, month, day, hour, minute, active);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(itemId);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeByte(active ? (byte) 1 : 0);
    }

    protected TodoReminder(Parcel in) {
        id = in.readLong();
        itemId = in.readLong();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        active = in.readByte() != 0;
    }

    public static Creator<TodoReminder> CREATOR = new Creator<TodoReminder>() {

        @Override
        public TodoReminder createFromParcel(Parcel source) {
            return new TodoReminder(source);
        }

        @Override
        public TodoReminder[] newArray(int size) {
            return new TodoReminder[size];
        }
    };

    public String toString() {
        return String.format("{id: %d, item_id: %d, year: %d, month: %d, day: %d, hour: %d, " +
                "minute: %d, active: %s}",
                id, itemId, year, month, day, hour, minute, active ? "true" : "false");
    }

}
