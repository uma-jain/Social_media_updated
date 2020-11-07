package ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

import Utils.MessageModel;
import Utils.UserModal;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyHolder> {
    Context context;
    List<MessagesAdapter> messageModelList;

    public MessagesAdapter(Context context, List<MessagesAdapter> messageModelList, UsersAdapter.OnUserClickListener onUserClickListener) {
        this.context = context;
        this.messageModelList = messageModelList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView imageView;
        TextView tv_username, tv_email;
        ImageView rowGoToChatActivity;

        UsersAdapter.OnUserClickListener onUserClickListener;

        public MyHolder(@NonNull View itemView, UsersAdapter.OnUserClickListener onUserClickListener) {
            super(itemView);

            TextView chatTextSent;
            TextView chatTextReceived;

                chatTextSent = itemView.findViewById(R.id.textview_chat_sent);
                chatTextReceived = itemView.findViewById(R.id.textview_chat_received);

            this.onUserClickListener = onUserClickListener;

        }

        @Override
        public void onClick(View view) {

        }
    }
}
