package edu.sjsu.android.productiv;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ToDoItemView extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private ToDoItem item;

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

        // find your UI elements
        TextView title = view.findViewById(R.id.itemName);
        TextView description  = view.findViewById(R.id.description);
        TextView priority = view.findViewById(R.id.priority);
        LinearLayout priorityBadge = view.findViewById(R.id.priorityBadge);

        // Gets the text priority
        title.setText(item.getName());
        description.setText(item.getDescription());
        priority.setText(Integer.toString(item.getPriority()));

        // Gets priority color
        setPriorityBadgeColor(priorityBadge, item.getPriority());

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