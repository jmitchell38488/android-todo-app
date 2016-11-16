package com.github.jmitchell38488.todo.app.data;

import com.google.gson.annotations.Expose;

public class TodoItem implements Cloneable {

    @Expose int id;

    @Expose String title;

    @Expose String description;

    @Expose int order;

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

    public TodoItem(int id, String title, String description, int order, boolean completed, boolean pinned) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order = order;
        this.completed = completed;
        this.pinned = pinned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
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
