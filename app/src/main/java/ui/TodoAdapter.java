package ui;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatabaseHandler;
import com.example.myapplication.R;

import com.google.android.material.snackbar.Snackbar;



import java.text.MessageFormat;
import java.util.List;

import Utils.Todo;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private Context context;
    private List<Todo> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public TodoAdapter(Context context, List<Todo> itemList){
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {
        Todo todo = itemList.get(position); //object item
        holder.todoNote.setText(todo.getTodoNote());
        holder.dateAdded.setText(MessageFormat.format("Date : {0}", todo.getDateTodoAdded()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView todoNote;
        public TextView dateAdded;
        public ImageView editButton;
        public ImageView deleteButton;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            todoNote = itemView.findViewById(R.id.todo_text);
            dateAdded = itemView.findViewById(R.id.item_date);

            editButton = itemView.findViewById(R.id.bt_edit);
            deleteButton = itemView.findViewById(R.id.bt_delete);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position;
            Todo item;
            position = getAdapterPosition();
            item = itemList.get(position);
            switch (view.getId()){
                case R.id.bt_edit:
                    //edit item

                    updateItem(item);
                    break;

                case R.id.bt_delete:
                    //delete item
                    deleteItem(item.getId());
                    break;
            }
        }

        //getAdapterPosition -- we are able to use this method because we are inside recyclerViewAdapter
        //                   -- this method gives us the exact position of the card whose delete button is being clicked
        //                   -- this ensures that we are deleting the correct item
        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_popup, null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);


            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id); // this will delete the item from the database
                    itemList.remove(getAdapterPosition()); // this will remove the item from the itemList
                    notifyItemRemoved(getAdapterPosition()); // this will notify the activity and remove the item using a simple
                    //inbuilt animation from item removing

                    dialog.dismiss();
                }
            });
        }

        public void updateItem(final Todo newItem)
        {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            Button saveButton;
            final EditText todoNote;
            TextView title;

            todoNote = view.findViewById(R.id.todoNotepopup);
            saveButton = view.findViewById(R.id.saveButton);

            saveButton.setText(R.string.update_text);
            title = view.findViewById(R.id.title);

            title.setText(R.string.edit_time);
            todoNote.setText(newItem.getTodoNote());

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //update our item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);

                    //update items
                    newItem.setTodoNote(todoNote.getText().toString().trim());

                    if (!todoNote.getText().toString().isEmpty()) {

                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem); //important!


                    }else {
                        Snackbar.make(view, "Fields Empty",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }

                    dialog.dismiss();

                }
            });

        }

    }
}
