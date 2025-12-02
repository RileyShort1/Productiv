package edu.sjsu.android.productiv;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class todo_list extends Fragment {

    private static boolean hasShownWelcomeThisSession = false;

    private ArrayList<ToDoItem> todoItems;
    private TodoListAdapter adapter;
    private ItemTouchHelper itemTouchHelper;

    private TodoItemDB database;

    private String aiText = "must be null";
    private boolean requestedOnce = false;
    private boolean waitingForAiResponse = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    private String getPrompt(ArrayList<ToDoItem> list) {
        StringBuilder prompt = new StringBuilder("Provide a short briefing for the tasks here that includes only task name, priority and time estimate as well as when you think they should be started (day, month non numeric) (max one sentence per item): ");
        for (int i = 0; i < list.size(); i++) {
            prompt.append(" Task Name: ");
            prompt.append(list.get(i).getName());
            prompt.append(" Task Desc: ");
            prompt.append(list.get(i).getDescription());
            prompt.append(" Task Due Date: ");
            prompt.append(list.get(i).getDueDate());
            prompt.append(" Task Priority: ");
            prompt.append(list.get(i).getPriority());
        }
        return prompt.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addTask = view.findViewById(R.id.addTaskButton);
        addTask.setOnClickListener(this::onAddTaskButtonClick);

        database = new TodoItemDB(requireContext());

        todoItems = database.getAllToDoItems();
        todoItems.sort(ToDoItem::compareTo);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.todo_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new TodoListAdapter(todoItems);
        recyclerView.setAdapter(adapter);

        // Set up item click listener
        adapter.setOnItemClickListener((item, position) -> {
            Bundle args = new Bundle();
            args.putSerializable("param1", item);
            Navigation.findNavController(view)
                    .navigate(R.id.action_todoListFragment_to_toDoItemView, args);
        });

        // Set up drag and drop with ItemTouchHelper
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set up drag listener - this connects the drag handle to ItemTouchHelper
        adapter.setOnStartDragListener(viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        });

        // Track when items are moved
        adapter.setOnItemMoveListener((fromPosition, toPosition) -> {
            // Update database order if needed
            // For now, the order is maintained in memory
        });

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (key, bundle) -> {
            ToDoItem newObject = (ToDoItem) bundle.getSerializable("result");
            if (newObject != null) {
                database.insert(newObject);
                addTodoItem(newObject);
            }
        });

        getParentFragmentManager().setFragmentResultListener("removeKey", this, (key, bundle) -> {
            ToDoItem newObject = (ToDoItem) bundle.getSerializable("itemToRemove");
            if (newObject != null) {
                database.remove(newObject);
                removeToDoItemByName(newObject);
            }
        });

        getParentFragmentManager().setFragmentResultListener("editListResult", this, (key, bundle) -> {
            String originalName = bundle.getString("originalName");
            ToDoItem updatedItem = (ToDoItem) bundle.getSerializable("updatedItem");
            if (originalName != null && updatedItem != null) {
                updateToDoItem(originalName, updatedItem);
            }
        });

        // Start the AI call as early as possible
        if (!requestedOnce) {
            requestedOnce = true;
            waitingForAiResponse = true;
            MainActivity act = (MainActivity) requireActivity();
            act.callAi(getPrompt(todoItems), text -> {
                if (!isAdded()) return;
                aiText = text;
                waitingForAiResponse = false;
                if (shouldShowWelcomeDialog()) {
                    showWelcomeDialog();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database != null && todoItems != null) {
            todoItems.clear();
            todoItems.addAll(database.getAllToDoItems());
            todoItems.sort(ToDoItem::compareTo);
            adapter.notifyDataSetChanged();
        }
        if (!waitingForAiResponse && shouldShowWelcomeDialog()) {
            showWelcomeDialog();
        }
    }

    private boolean shouldShowWelcomeDialog() {
        return !hasShownWelcomeThisSession && todoItems != null && !todoItems.isEmpty();
    }

    private void showWelcomeDialog() {
        if (shouldShowWelcomeDialog()) {
            hasShownWelcomeThisSession = true;
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("Productiv AI Task Overview")
                    .setMessage(aiText)
                    .setPositiveButton("Dismiss", (d, which) -> {
                        d.dismiss();
                    })
                    .setNeutralButton("Refresh", (d, which) -> {
                        // Refresh button clicked - will be handled below
                    })
                    .setCancelable(false)
                    .create();
            
            dialog.show();
            
            // Override the refresh button to call AI again
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                refreshAiResponse(dialog);
            });
        }
    }

    private void refreshAiResponse(AlertDialog dialog) {
        if (todoItems == null || todoItems.isEmpty()) {
            Toast.makeText(getContext(), "No tasks to analyze", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable refresh button while loading
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        dialog.setMessage("Refreshing AI response...");
        
        waitingForAiResponse = true;
        MainActivity act = (MainActivity) requireActivity();
        act.callAi(getPrompt(todoItems), text -> {
            if (!isAdded() || dialog == null || !dialog.isShowing()) return;
            
            aiText = text;
            waitingForAiResponse = false;
            dialog.setMessage(aiText);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
        });
    }

    public void onAddTaskButtonClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_todoListFragment_to_addNewItem);
    }

    public void addTodoItem(ToDoItem item) {
        todoItems.add(item);
        adapter.notifyDataSetChanged();
    }

    public void removeToDoItemByName(ToDoItem item) {
        for (int i = 0; i < todoItems.size(); i++) {
            if (todoItems.get(i).getName().equals(item.getName())) {
                todoItems.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    private void updateToDoItem(String originalName, ToDoItem updatedItem) {
        for (int i = 0; i < todoItems.size(); i++) {
            if (todoItems.get(i).getName().equals(originalName)) {
                todoItems.set(i, updatedItem);
                adapter.notifyItemChanged(i);
                break;
            }
        }
        todoItems.sort(ToDoItem::compareTo);
        adapter.notifyDataSetChanged();
    }
}