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

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

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
        View view;

        if(viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatright, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatleft, parent, false);


        return new MyHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String senderid= messageModelList.get(position).getSendBy();
        String sendername= messageModelList.get(position).getSenderName();

        String msg=messageModelList.get(position).getMessageText();
        String time=messageModelList.get(position).getMessageTime();

     //   Log.i("msg",msg);
     //   Log.i("msg",senderid);

        holder.message.setText(msg);
        holder.sender.setText(sendername);
        holder.timestamp.setText(time.substring(9,17));

    }

    @Override
    public int getItemCount() {

        return messageModelList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if( userApi.getUid().equals(messageModelList.get(position).getSendBy()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }



    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView sender, message, timestamp;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sendername);
            message = itemView.findViewById(R.id.textmessage);
            timestamp = itemView.findViewById(R.id.timestamp);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
