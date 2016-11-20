package com.github.jmitchell38488.todo.app.data.repository;

import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;

import java.util.List;

import rx.Observable;

public interface TodoItemRepository {

    List<TodoItem> getAllItems();

    List<TodoItem> getAllItems(Sort sort, Filter filter);

    List<TodoItem> getAllCompletedItems();

    List<TodoItem> getAllNotCompletedItems();

    TodoItem getItem(int id);

    void saveTodoItem(TodoItem item);

    void deleteTodoItem(TodoItem item);
}
