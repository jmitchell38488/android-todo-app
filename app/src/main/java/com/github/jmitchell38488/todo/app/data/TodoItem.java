package com.github.jmitchell38488.todo.app.data;

import com.google.gson.annotations.Expose;

public class TodoItem {

    @Expose int id;

    @Expose String title;

    @Expose String description;

    @Expose int order;

    @Expose boolean completed;

    public TodoItem() {}

    public TodoItem(int id, String title, String description, int order, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.order = order;
        this.completed = completed;
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
}
