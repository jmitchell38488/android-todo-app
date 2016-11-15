package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.helper.SimpleItemTouchHelperCallback;

import java.util.List;

import javax.inject.Inject;

public class ListFragment extends Fragment implements OnStartDragListener, RecyclerListAdapter.ListChangeListener {

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Inject TodoStorage todoStorage;

    private static String POSITION_KEY = "position";
    private int mPosition;

    private RecyclerListAdapter.ListClickListener onClick =
            new RecyclerListAdapter.ListClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    int iposition = mRecyclerView.getChildLayoutPosition(view);
                    TodoItem item = mAdapter.getItem(iposition);
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence("title", item.getTitle());
                    arguments.putCharSequence("description", item.getDescription());
                    arguments.putInt("position", position);
                    arguments.putBoolean("edit", true);
                    arguments.putBoolean("pinned", item.isPinned());
                    arguments.putBoolean("completed", item.isCompleted());

                    mPosition = position;

                    ((ListActivity) getActivity()).showEditDialog(arguments);
                }

            };

    private View.OnClickListener onCompleteClick =
            new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    View rootView = (View) view.getParent() // Frame Layout
                            .getParent() // Linear Layout
                            .getParent() // Linear Layout
                            .getParent(); // Linear Layout

                    int iposition = mRecyclerView.getChildLayoutPosition(rootView);

                    if (iposition != RecyclerView.NO_POSITION) {
                        TodoItem item = mAdapter.getItem(iposition);

                        item.setCompleted(!item.isCompleted());

                        if (item.isCompleted()) {
                            item.setPinned(false);
                        }

                        replaceAdapterItem(iposition, item);
                    } else {
                        Toast toast = Toast.makeText(ListFragment.this.getActivity(), getString(R.string.invalid_list_position), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*View drawer = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView) drawer.findViewById(R.id.list_container);*/
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TodoApp.getComponent(getActivity()).inject(this);

        // Set adapter
        mAdapter = new RecyclerListAdapter(getActivity(), todoStorage, this, this, onClick, onCompleteClick);

        // Set view
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set touch helper
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Scroll to previous position
        if (mPosition != ListView.INVALID_POSITION) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList) {
        // Set the new sorted list
        ((RecyclerListAdapter) mRecyclerView.getAdapter()).setListData(newList);

        // Notify data changed, invalidate and reset the layout manager
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.invalidate();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemChange(int position) {
        mRecyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            mPosition = getItemPositionFromYOffset();
        }

        outState.putInt(POSITION_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    private int getItemPositionFromYOffset() {
        int[] heights = new int[mRecyclerView.getChildCount()];

        View v = mRecyclerView.getChildAt(0);
        if (v == null) {
            return 0;
        }

        LinearLayoutManager lMan = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        int firstVisible = lMan.findFirstCompletelyVisibleItemPosition();
        if (firstVisible < mAdapter.getItemCount() && heights[firstVisible + 1] == 0) {
            heights[firstVisible + 1] += heights[firstVisible] + v.getHeight();
        }

        return v.getTop() + heights[firstVisible];
    }

    public TodoItem getItemFromAdapter(int position) {
        return mAdapter.getItem(position);
    }

    public void replaceAdapterItem(int position, TodoItem item) {
        mAdapter.replace(position, item);
        mRecyclerView.getAdapter().notifyItemChanged(position);
    }

    public void addItem(TodoItem item) {
        int newPosition = item.isPinned() ? 0 : mAdapter.getFirstUnpinnedPosition();
        mAdapter.addItem(newPosition, item);
        mRecyclerView.getAdapter().notifyItemInserted(newPosition);
        mRecyclerView.smoothScrollToPosition(newPosition);
    }

}
