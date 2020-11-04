package ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Utils.UserModal;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.MyHolder> {
    Context context;
     List<UserModal> userModelList;
     private OnUserClickListener onUserClickListener;

    //constructor
    public UsersAdapter(Context context, List<UserModal> userModelList, OnUserClickListener onUserClickListener) {
        this.context = context;
        this.userModelList = userModelList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        return new MyHolder(view, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int i) {
        final String UserImage=userModelList.get(i).getImage();
        String Username=userModelList.get(i).getUsername();
        String email=userModelList.get(i).getEmail();
        //set data
        holder.tv_username.setText(Username);
        holder.tv_email.setText(email);
                 //set image
        try {
            Picasso.get().load(UserImage).into(holder.imageView);
        } catch (Exception e) {
            Log.d("info", "set dfault image");
            Picasso.get().load(R.drawable.drippimg_uma).into(holder.imageView);
        }

        //hanlde item click
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context,userModelList.get(i).getUid(),Toast.LENGTH_LONG).show();
//                //go to mainactivity2
//
////              Intent intent = new Intent(context, MainActivity2.class);
////              intent.putExtra("uid", userModelList.get(i).getUid());
////              context.startActivity(intent);
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    class  MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView imageView;
        TextView tv_username,tv_email;
        ImageView rowGoToChatActivity;

        OnUserClickListener onUserClickListener;

        public MyHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            //initiate views
            imageView=itemView.findViewById(R.id.row_user_civ_profilepic);
            tv_email=itemView.findViewById(R.id.row_user_email);
            tv_username=itemView.findViewById(R.id.row_user_username);
            rowGoToChatActivity = itemView.findViewById(R.id.row_user_goto_chatActivity);
            this.onUserClickListener = onUserClickListener;

           rowGoToChatActivity.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.onUserClick(getAdapterPosition());
        }
    }

    public interface OnUserClickListener
    {
        void onUserClick(int position);
    }


}
