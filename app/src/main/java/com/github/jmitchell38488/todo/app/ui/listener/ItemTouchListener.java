package com.github.jmitchell38488.todo.app.ui.listener;

public interface ItemTouchListener {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemSwipeRight(int position);

    void onItemSwipeLeft(int position);

}