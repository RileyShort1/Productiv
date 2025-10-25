package edu.sjsu.android.productiv;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

public class ToDoItem implements Serializable {
    private String name;
    private String description;
    private LocalDate dueDate;
    private int priority; // 1-5 priority level

    public ToDoItem(String name, String description, LocalDate dueDate, int priority) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public LocalDate getDueDate() {
        return this.dueDate;
    }
    public int getPriority() {
        return this.priority;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
