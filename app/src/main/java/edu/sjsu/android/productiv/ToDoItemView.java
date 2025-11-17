package edu.sjsu.android.productiv;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;

public class ToDoItemView extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private ToDoItem item;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    public ToDoItemView() {
        // Required empty public constructor
    }
    public static ToDoItemView newInstance(ToDoItem param1) {
        ToDoItemView fragment = new ToDoItemView();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (getArguments() != null) {
            item = (ToDoItem) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do_item_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView title = view.findViewById(R.id.itemName);
        final TextView description  = view.findViewById(R.id.description);
        final TextView priorityView = view.findViewById(R.id.priority);
        final LinearLayout priorityBadge = view.findViewById(R.id.priorityBadge);
        final TextView dueDateField = view.findViewById(R.id.dueDate);

        Button completeButton = view.findViewById(R.id.addButton);
        completeButton.setOnClickListener(this::completeItem);

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("itemToEdit", item);
            Navigation.findNavController(v).navigate(R.id.action_toDoItemView_to_toDoItemEdit, args);
        });

        bindItemToUi(title, description, priorityView, dueDateField, priorityBadge);

        getParentFragmentManager().setFragmentResultListener("editResult", this, (key, bundle) -> {
            ToDoItem updatedItem = (ToDoItem) bundle.getSerializable("updatedItem");
            if (updatedItem != null) {
                item = updatedItem;
                bindItemToUi(title, description, priorityView, dueDateField, priorityBadge);
            }
        });
    }

    private void bindItemToUi(TextView title,
                              TextView description,
                              TextView priority,
                              TextView dueDateField,
                              LinearLayout priorityBadge) {
        if (item == null) {
            return;
        }
        title.setText(item.getName());
        description.setText(item.getDescription());
        priority.setText(Integer.toString(item.getPriority()));
        dueDateField.setText(item.getDueDate().format(formatter));
        setPriorityBadgeColor(priorityBadge, item.getPriority());
    }

    public void completeItem(View view) {
        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
        // send current item back to remove from db
        Bundle result = new Bundle();
        result.putSerializable("itemToRemove", item);
        getParentFragmentManager().setFragmentResult("removeKey", result);
        Navigation.findNavController(view).navigate(R.id.action_toDoItemView_to_todoListFragment);
    }

    // Changes color of priority
    private void setPriorityBadgeColor(LinearLayout priorityBadge, int priorityLevel) {
        GradientDrawable drawable = (GradientDrawable) priorityBadge.getBackground();

        String color;
        switch (priorityLevel) {
            case 1:
                color = "#b0170c"; // Dark Red
                break;
            case 2:
                color = "#ff841f"; // Orange
                break;
            case 3:
                color = "#e2e60b"; // Yellow
                break;
            case 4:
                color = "#1ac42b"; // Green
                break;
            default:
                color = "#777877"; // Gray
                break;
        }

        drawable.setColor(Color.parseColor(color));
    }

}