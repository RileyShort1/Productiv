package edu.sjsu.android.productiv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoItemDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todoItems";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "item";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_PRIORITY = "priority";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DUE_DATE + " TEXT, " +
                    COLUMN_PRIORITY + " INTEGER" +
                    ");";


    public TodoItemDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insert(ToDoItem item) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        // insert obj attributes
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_DUE_DATE, item.getDueDate().toString());
        values.put(COLUMN_PRIORITY, item.getPriority());

        long id = database.insert(TABLE_NAME, null, values);
        //database.close();
        return id;
    }

    public boolean remove(ToDoItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_NAME,
                "name = ?",
                new String[]{String.valueOf(item.getName())}
        );
        //db.close();
        return rowsDeleted > 0;
    }

    public ArrayList<ToDoItem> getAllToDoItems() {
        ArrayList<ToDoItem> list = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, COLUMN_PRIORITY + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                LocalDate dueDate = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)));
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));

                ToDoItem item = new ToDoItem(name, desc, dueDate, priority);
                list.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        //database.close();
        return list;
    }

    public boolean update(String originalName, ToDoItem updatedItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, updatedItem.getName());
        values.put(COLUMN_DESCRIPTION, updatedItem.getDescription());
        values.put(COLUMN_DUE_DATE, updatedItem.getDueDate().toString());
        values.put(COLUMN_PRIORITY, updatedItem.getPriority());

        int rowsUpdated = db.update(
                TABLE_NAME,
                values,
                COLUMN_NAME + " = ?",
                new String[]{originalName}
        );

        return rowsUpdated > 0;
    }


}
