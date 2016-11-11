package com.github.jmitchell38488.todo.app.data;

public class TodoItem {

    private String title;

    private String description;

    private int order;

    private boolean completed;

    public TodoItem() {
    }

    public TodoItem(String title, String description, int order, boolean completed) {
        this.title = title;
        this.description = description;
        this.order = order;
        this.completed = completed;
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
