package ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import Utils.MessageModel;
import Utils.UserApi;
import Utils.UserModal;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyHolder> {
    Context context;
    List<MessageModel> messageModelList;
    UserApi userApi=UserApi.getInstance();

    public MessagesAdapter(Context context, List<MessageModel> messageModelList){
        this.context = (Context) context;
        this.messageModelList = messageModelList;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_msg, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String senderid= messageModelList.get(position).getSendBy();
        String msg=messageModelList.get(position).getMessageText();
        String time=messageModelList.get(position).getMessageTime();
        Log.i("msg",msg);
        Log.i("msg",senderid);
        if(senderid.equals(userApi.getUid())){
            //sender is current user
            Log.i("msg","i am sender");
            holder.chat_sent.setText(msg);
            holder.time_sent.setText(time.substring(9,17));
            holder.l_receieved.setVisibility(View.GONE);
        }
        else{

            Log.i("msg","i am not sender");
            holder.chat_received.setText(msg);
            holder.time_received.setText(time.substring(9,17));
            holder.l_sent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {

        return messageModelList.size();
    }



    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView imageView;
        TextView tv_username, tv_email;
        LinearLayout l_receieved,l_sent;
        TextView chat_sent,time_sent;
        TextView chat_received,time_received;

        ImageView rowGoToChatActivity;
        UsersAdapter.OnUserClickListener onUserClickListener;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
               l_receieved=itemView.findViewById(R.id.linear_layout_received);
               l_sent=itemView.findViewById(R.id.linear_layout_sent);

                chat_sent = itemView.findViewById(R.id.message_text_view_sent);
                chat_received = itemView.findViewById(R.id.textview_chat_recieved);

                time_received=itemView.findViewById(R.id.timestamp_text_view_receieved);
                time_sent=itemView.findViewById(R.id.timestamp_text_sent);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
