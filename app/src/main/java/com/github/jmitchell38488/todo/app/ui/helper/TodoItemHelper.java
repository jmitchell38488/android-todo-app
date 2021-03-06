package com.github.jmitchell38488.todo.app.ui.helper;

import android.app.Activity;

import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TodoItemHelper {

    private static final PublishSubject<CompletedEvent> mObservableSubjectComplete = PublishSubject.create();
    private static final PublishSubject<RemovedEvent> mObservableSubjectRemove = PublishSubject.create();
    private static final PublishSubject<AddItemListEvent> mObservableSubjectItemList = PublishSubject.create();

    private final Activity mActivity;
    private final TodoItemRepository mRepository;

    public TodoItemHelper(Activity activity, TodoItemRepository repository) {
        mActivity = activity;
        mRepository = repository;
    }

    public Observable<CompletedEvent> getCompletedObservable() {
        return mObservableSubjectComplete.asObservable();
    }

    public Observable<RemovedEvent> getRemovedObservable() {
        return mObservableSubjectRemove.asObservable();
    }

    public Observable<AddItemListEvent> getListObservable() {
        return mObservableSubjectItemList.asObservable();
    }

    public void setItemComplete(long id) {
        mObservableSubjectComplete.onNext(new CompletedEvent(id));
    }

    public void setItemRemoved(long id) {
        mObservableSubjectRemove.onNext(new RemovedEvent(id));
    }

    public void addItemsToList(List<TodoItem> list) {
        mObservableSubjectItemList.onNext(new AddItemListEvent(list));
    }

    public static class CompletedEvent {
        public long itemId;

        private CompletedEvent(long itemId) {
            this.itemId = itemId;
        }
    }

    public static class RemovedEvent {
        public long itemId;

        private RemovedEvent(long itemId) {
            this.itemId = itemId;
        }
    }

    public static class AddItemListEvent {
        public List<TodoItem> list;

        private AddItemListEvent(List<TodoItem> list) {
            this.list = list;
        }
    }

}
