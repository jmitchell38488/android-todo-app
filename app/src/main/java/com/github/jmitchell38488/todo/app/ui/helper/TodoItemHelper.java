package com.github.jmitchell38488.todo.app.ui.helper;

import android.app.Activity;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TodoItemHelper {

    private static final PublishSubject<CompletedEvent> SUBJECT_COMPLETED = PublishSubject.create();
    private static final PublishSubject<RemovedEvent> SUBJECT_REMOVED = PublishSubject.create();

    private final Activity mActivity;
    private final TodoItemRepository mRepository;

    public TodoItemHelper(Activity activity, TodoItemRepository repository) {
        mActivity = activity;
        mRepository = repository;
    }

    public Observable<CompletedEvent> getCompletedObservable() {
        return SUBJECT_COMPLETED.asObservable();
    }

    public Observable<RemovedEvent> getRemovedObservable() {
        return SUBJECT_REMOVED.asObservable();
    }

    public void setItemComplete(TodoItem item, boolean complete) {

    }

    public void setItemRemoved(TodoItem item, boolean removed) {

    }

    public static class CompletedEvent {
        public long itemId;
        public boolean completed;

        private CompletedEvent(long itemId, boolean completed) {
            this.itemId = itemId;
            this.completed = completed;
        }
    }

    public static class RemovedEvent {
        public long itemId;
        public boolean removed;

        private RemovedEvent(long itemId, boolean removed) {
            this.itemId = itemId;
            this.removed = removed;
        }
    }

}
