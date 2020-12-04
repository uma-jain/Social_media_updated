package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utils.Todo;
import ui.TodoAdapter;

public class TodoActivity extends AppCompatActivity {

    List<Todo> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Button saveButton;
    private EditText todoNote;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });

        databaseHandler = new DatabaseHandler(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        //get item from db
        itemList = new ArrayList<>();

        //if there is not item in data base then i want to show message no items
        //Toast.makeText(this, databaseHandler.getItemsCount(), Toast.LENGTH_SHORT).show();
        if (databaseHandler.getItemsCount()>0)
        {
            itemList = databaseHandler.getAllItems();

            TodoAdapter todoAdapter = new TodoAdapter(this, itemList);
            recyclerView.setAdapter(todoAdapter);
            todoAdapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(this, "No items in db", Toast.LENGTH_SHORT).show();
        }



    }

    private void createPopupDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);

        todoNote = view.findViewById(R.id.todoNotepopup);
        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!todoNote.getText().toString().isEmpty())
                {
                    saveItem(view);
                }

                else
                {
                    Snackbar.make(view, "Please enter values in all fields", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveItem(View view) {
        //Todo : save each item to db
        Todo item = new Todo();

        String todoText = todoNote.getText().toString().trim();

        item.setTodoNote(todoText);
        databaseHandler.addTodo(item);

        Snackbar.make(view, "Item saved", Snackbar.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();

                //Todo : move to next screen - details screen
                startActivity(new Intent(TodoActivity.this, TodoActivity.class));
                finish();//this will kill the previous activity

            }
        },1200);
    }
}