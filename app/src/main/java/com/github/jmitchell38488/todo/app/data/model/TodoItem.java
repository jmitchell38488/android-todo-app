package com.github.jmitchell38488.todo.app.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.jmitchell38488.todo.app.data.provider.meta.TodoItemMeta;
import com.google.gson.annotations.Expose;

public class TodoItem implements Cloneable, Parcelable, TodoItemMeta {

    @Expose long id;

    @Expose String title;

    @Expose String description;

    @Expose long order;

    @Expose boolean completed;

    @Expose boolean pinned;

    // We don't want to keep any of these public properties
    @Expose(serialize = false, deserialize = false)
    public int width;

    @Expose(serialize = false, deserialize = false)
    public int height;

    @Expose(serialize = false, deserialize = false)
    public int sX;

    @Expose(serialize = false, deserialize = false)
    public int sY;

    public TodoItem() {
        completed = false;
        pinned = false;
    }

    public TodoItem(long id, String title, String description, long order, boolean completed, boolean pinned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order = order;
        this.completed = completed;
        this.pinned = pinned;
    }

    public long getId() {
        return id;
    }

    public TodoItem setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TodoItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TodoItem setDescription(String description) {
        this.description = description;
        return this;
    }

    public long getOrder() {
        return order;
    }

    public TodoItem setOrder(long order) {
        this.order = order;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TodoItem setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    public boolean isPinned() {
        return pinned;
    }

    public TodoItem setPinned(boolean pinned) {
        this.pinned = pinned;
        return this;
    }

    @Override
    public Object clone() {
        TodoItem t = new TodoItem(id, title, description, order, completed, pinned);
        t.height = height;
        t.width = width;
        t.sX = sX;
        t.sY = sY;

        return t;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(order);
        dest.writeByte(completed ? (byte) 1 : 0);
        dest.writeByte(pinned ? (byte) 1 : 0);
    }

    protected TodoItem(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        order = in.readLong();
        completed = in.readByte() != 0;
        pinned = in.readByte() != 0;
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {

        @Override
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }

    };
}
