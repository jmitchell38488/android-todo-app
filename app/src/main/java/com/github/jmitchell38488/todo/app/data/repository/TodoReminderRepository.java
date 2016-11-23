package com.github.jmitchell38488.todo.app.data.repository;

import com.github.jmitchell38488.todo.app.data.model.TodoReminder;

import java.util.List;

public interface TodoReminderRepository {

    List<TodoReminder> getAllByTodoItemId(long itemId);

    TodoReminder getReminderById(long id);

    void saveTodoReminder(TodoReminder todoReminder);

    void deleteTodoReminder(TodoReminder todoReminder);

}
