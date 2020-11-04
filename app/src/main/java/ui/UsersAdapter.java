package ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Personal_Chat_Activity;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Utils.UserModal;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.MyHolder> {
    Context context;
     List<UserModal> userModelList;

    //constructor
    public UsersAdapter(Context context, List<UserModal> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_user, parent, false);
        return new MyHolder(view);
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
        holder.imageView_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,userModelList.get(i).getUid()+"got to chat act",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(context, Personal_Chat_Activity.class);
              intent.putExtra("hisUid",userModelList.get(i).getUid());
              context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    class  MyHolder extends RecyclerView.ViewHolder{

        CircleImageView imageView;
        ImageView imageView_msg;

        TextView tv_username,tv_email;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //initiate views
            imageView=itemView.findViewById(R.id.row_user_civ_profilepic);
             imageView_msg=itemView.findViewById(R.id.row_user_goto_chatActivity);
            tv_email=itemView.findViewById(R.id.row_user_email);
            tv_username=itemView.findViewById(R.id.row_user_username);
        }

    }


}
