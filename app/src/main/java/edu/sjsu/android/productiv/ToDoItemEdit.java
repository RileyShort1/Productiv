package edu.sjsu.android.productiv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ToDoItemEdit extends Fragment {

    private static final String ARG_ITEM = "itemToEdit";

    private ToDoItem currentItem;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private TodoItemDB database;

    public ToDoItemEdit() {
        // Required empty public constructor
    }

    public static ToDoItemEdit newInstance(ToDoItem item) {
        ToDoItemEdit fragment = new ToDoItemEdit();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (ToDoItem) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do_item_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentItem == null) {
            Navigation.findNavController(view).popBackStack();
            return;
        }

        database = new TodoItemDB(requireContext());

        EditText nameField = view.findViewById(R.id.itemName);
        EditText descriptionField = view.findViewById(R.id.description);
        EditText dueDateField = view.findViewById(R.id.dueDate);
        EditText priorityField = view.findViewById(R.id.priority);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        nameField.setText(currentItem.getName());
        descriptionField.setText(currentItem.getDescription());
        dueDateField.setText(currentItem.getDueDate().format(formatter));
        priorityField.setText(String.valueOf(currentItem.getPriority()));

        long initialSelection = currentItem.getDueDate()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        MaterialDatePicker<Long> picker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select due date")
                        .setSelection(initialSelection)
                        .build();

        dueDateField.setOnClickListener(v -> picker.show(getParentFragmentManager(), "editDueDate"));
        picker.addOnPositiveButtonClickListener(selection -> {
            LocalDate date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
            dueDateField.setText(date.format(formatter));
        });

        saveButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String description = descriptionField.getText().toString().trim();
            String dueDateText = dueDateField.getText().toString().trim();
            String priorityText = priorityField.getText().toString().trim();

            if (name.isEmpty()) {
                nameField.setError("Name is required");
                return;
            }
            if (dueDateText.isEmpty()) {
                dueDateField.setError("Due date is required");
                return;
            }

            LocalDate dueDate;
            try {
                dueDate = LocalDate.parse(dueDateText, formatter);
            } catch (DateTimeParseException ex) {
                dueDateField.setError("Use MM/dd/yyyy");
                return;
            }

            int priority = 4;
            if (!priorityText.isEmpty()) {
                try {
                    priority = Integer.parseInt(priorityText);
                } catch (NumberFormatException ex) {
                    priorityField.setError("Enter a number");
                    return;
                }
            }

            ToDoItem updatedItem = new ToDoItem(name, description, dueDate, priority);
            boolean success = database.update(currentItem.getName(), updatedItem);
            if (!success) {
                Toast.makeText(requireContext(), "Unable to save changes", Toast.LENGTH_SHORT).show();
                return;
            }

            Bundle result = new Bundle();
            result.putSerializable("updatedItem", updatedItem);
            result.putString("originalName", currentItem.getName());

            getParentFragmentManager().setFragmentResult("editResult", result);
            getParentFragmentManager().setFragmentResult("editListResult", result);
            Navigation.findNavController(view).popBackStack();
        });

        cancelButton.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }
}

