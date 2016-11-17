package com.github.jmitchell38488.todo.app.ui.listener;

/**
 * Created by justinmitchell on 17/11/2016.
 */

public interface ItemStateChangeListener {

    /**
     * This will handle any events when the item's state changes from active to complete, or
     * complete to active
     * @param position
     */
    public void onItemComplete(int position);

    /**
     * This will handle any events when the item's state changes from either active or complete
     * to removed
     * @param position
     */
    public void onItemDismiss(int position);

}
