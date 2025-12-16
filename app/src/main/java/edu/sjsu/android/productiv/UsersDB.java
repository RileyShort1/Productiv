package edu.sjsu.android.productiv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class UsersDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "usersDatabase";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "users";
    private static final String USER_ID = "_user_id";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    static final String CREATE_TABLE =
            String.format("CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL);", TABLE_NAME, USER_ID, NAME, EMAIL, PASSWORD);

    public UsersDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllUsers(String orderBy) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(TABLE_NAME,
                new String[]{USER_ID, NAME, EMAIL, PASSWORD},
                null, null, null, null, orderBy);
    }
}
