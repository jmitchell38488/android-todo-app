package com.github.jmitchell38488.todo.app.data.repository;

import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import java.util.List;

import rx.Observable;

public interface TodoItemRepository {

    public final int VISIBLE_THRESHOLD = 10;

    Observable<List<TodoItem>> getAllItems();

    Observable<List<TodoItem>> getAllItems(Sort sort, Filter filter);

    Observable<List<TodoItem>> getAllCompletedItems();

    Observable<List<TodoItem>> getAllNotCompletedItems();

    Observable<List<TodoItem>> getItems(int page, Sort sort, Filter filter);

    void saveTodoItem(TodoItem item);

    void deleteTodoItem(TodoItem item);
}
