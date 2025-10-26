package edu.sjsu.android.productiv;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.ListFragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.time.LocalDate;
import java.util.ArrayList;

public class todo_list extends ListFragment {

    private ArrayList<ToDoItem> todoItems;
    private ArrayAdapter<ToDoItem> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // place holder list just for view
        todoItems = new ArrayList<>();

        todoItems.add(new ToDoItem("CS175 HW", "Complete the hw", LocalDate.now(), 4));
        todoItems.add(new ToDoItem("CS147 HW", "do the lab assignment", LocalDate.now(), 2));
        todoItems.add(new ToDoItem("CS157C HW", "Complete Midterm", LocalDate.now(), 1));

        todoItems.sort(ToDoItem::compareTo);
        // Create and set the adapter
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, todoItems);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // Handle item click, implement edit/delete functionality here probably
        ToDoItem selectedItem = todoItems.get(position);

        Bundle args = new Bundle();
        args.putSerializable("selectedItem", selectedItem);

        Navigation.findNavController(v)
                .navigate(R.id.action_todoListFragment_to_toDoItemView, args);
    }

    // todo item can call this later when implementing add functionalit
    public void addTodoItem(ToDoItem item) {
        todoItems.add(item);
        adapter.notifyDataSetChanged();
    }

    // todo remove item
    public void removeTodoItem(int position) {
        if (position >= 0 && position < todoItems.size()) {
            todoItems.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}