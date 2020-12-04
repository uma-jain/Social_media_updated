package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Utils.Constants;
import Utils.Todo;

public class DatabaseHandler extends SQLiteOpenHelper {
    private final Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create table todo_tb1 (id, todo_note , date_added);
        String CREATE_Todo_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_TODO_NOTE + " TEXT," + Constants.KEY_TODO_ADDED + " LONG);";

        sqLiteDatabase.execSQL(CREATE_Todo_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop table if exists todo_tb1
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    //CRUD operations
    public void addTodo(Todo todo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_TODO_NOTE, todo.getTodoNote());
        values.put(Constants.KEY_TODO_ADDED, java.lang.System.currentTimeMillis());//timestamp of java lang

        //insert the row
        //insert into todo_tb1 values(id, todo_note, date_added);
        db.insert(Constants.TABLE_NAME, null, values);
    }

    //Get an Item
    public Todo getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_TODO_NOTE},
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Todo todo = new Todo();
        if (cursor != null) {
            todo.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            todo.setTodoNote(cursor.getString(cursor.getColumnIndex(Constants.KEY_TODO_NOTE)));

            //convert Timestamp to something readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_TODO_ADDED)))
                    .getTime()); // Feb 23, 2020

            todo.setDateTodoAdded(formattedDate);
        }

        return todo;
    }

    //Get all Items
    public List<Todo> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Todo> itemList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_TODO_NOTE,
                        Constants.KEY_TODO_ADDED},
                null, null, null, null,
                Constants.KEY_TODO_ADDED+ " DESC");

        if (cursor.moveToFirst()) {
            do {
                Todo todo = new Todo();
                todo.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                todo.setTodoNote(cursor.getString(cursor.getColumnIndex(Constants.KEY_TODO_NOTE)));

                //convert Timestamp to something readable
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_TODO_ADDED)))
                        .getTime()); // Feb 23, 2020
                todo.setDateTodoAdded(formattedDate);

                //Add to arraylist
                itemList.add(todo);
            } while (cursor.moveToNext());
        }
        return itemList;

    }

    //Todo: Add updateItem
    public int updateItem(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_TODO_NOTE, todo.getTodoNote());
        values.put(Constants.KEY_TODO_ADDED, java.lang.System.currentTimeMillis());//timestamp of the system

        //update row
        //update todo_tb1 set todo_note="some value" where id = todo.getId()
        return db.update(Constants.TABLE_NAME, values,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(todo.getId())});
    }

    //Todo: Add Delete Item
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //delete from todo_tb1 where id = todo.getId()
        db.delete(Constants.TABLE_NAME,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        //close
        db.close();
    }

    //Todo: getItemCount
    public int getItemsCount() {
        //select * from todo_tb1
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        //here i get the count of table
        return cursor.getCount();
    }
}