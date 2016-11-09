package com.github.jmitchell38488.todo.app.data;

/**
 * Created by justinmitchell on 10/11/2016.
 */

public class TodoItem {

    private String title;

    private String description;

    private String dateCreated;

    private String dateDue;

    private int order;

    private boolean completed;

    public TodoItem() {
    }

    public TodoItem(String title, String description, String dateCreated, String dateDue, int order, boolean completed) {
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.dateDue = dateDue;
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
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
