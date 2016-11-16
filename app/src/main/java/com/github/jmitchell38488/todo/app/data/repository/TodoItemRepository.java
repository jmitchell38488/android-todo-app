package com.github.jmitchell38488.todo.app.data.repository;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import java.util.List;

import rx.Observable;

/**
 * Created by justinmitchell on 16/11/2016.
 */

public interface TodoItemRepository {

    Observable<List<TodoItem>> getAllItems();

    Observable<List<TodoItem>> getAllCompletedItems();

    Observable<List<TodoItem>> getAllNotCompletedItems();

    void saveTodoItem(TodoItem item);

    void deleteTodoItem(TodoItem item);
}
