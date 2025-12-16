package edu.sjsu.android.productiv;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
// Adapter for todolist
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    private ArrayList<ToDoItem> items;
    private OnItemClickListener clickListener;
    private OnStartDragListener dragListener;
    private OnItemMoveListener moveListener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public interface OnItemClickListener {
        void onItemClick(ToDoItem item, int position);
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemMoveListener {
        void onItemMoved(int fromPosition, int toPosition);
    }

    public TodoListAdapter(ArrayList<ToDoItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnStartDragListener(OnStartDragListener listener) {
        this.dragListener = listener;
    }

    public void setOnItemMoveListener(OnItemMoveListener listener) {
        this.moveListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // For moving the order of item when dragging
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        if (moveListener != null) {
            moveListener.onItemMoved(fromPosition, toPosition);
        }
    }

    public ArrayList<ToDoItem> getItems() {
        return items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView dueDate;
        TextView priorityBadge;
        View dragHandle;

        ViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            dueDate = itemView.findViewById(R.id.task_due_date);
            priorityBadge = itemView.findViewById(R.id.priority_badge);
            dragHandle = itemView.findViewById(R.id.drag_handle);
        }

        //
        @SuppressLint("ClickableViewAccessibility")
        void bind(ToDoItem item, int position) {
            taskName.setText(item.getName());
            dueDate.setText(item.getDueDate().format(formatter));
            priorityBadge.setText(String.valueOf(item.getPriority()));

            // Set priority color
            String color = getPriorityColor(item.getPriority());
            priorityBadge.setBackgroundColor(Color.parseColor(color));

            // CLick on main area
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(item, getBindingAdapterPosition());
                }
            });

            // Touch and drag on the drag handle area
            dragHandle.setOnTouchListener((v, event) -> {
                if (dragListener != null) {
                    dragListener.onStartDrag(ViewHolder.this);
                }
                return false;
            });
        }

        // Get priority color for all items
        private String getPriorityColor(int priority) {
            switch (priority) {
                case 1: return "#b0170c"; // Dark Red
                case 2: return "#ff841f"; // Orange
                case 3: return "#e2e60b"; // Yellow
                case 4: return "#1ac42b"; // Green
                default: return "#777877"; // Gray
            }
        }
    }
}