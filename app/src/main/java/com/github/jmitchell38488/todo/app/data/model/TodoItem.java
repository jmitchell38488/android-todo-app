package com.github.jmitchell38488.todo.app.data.model;

import com.google.gson.annotations.Expose;

public class TodoItem implements Cloneable {

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
}
