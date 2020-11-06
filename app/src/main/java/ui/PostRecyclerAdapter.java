package ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import Utils.PostModel;
import fragments.CommentsFragment;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private List<PostModel> postModelList;
    private OnPostClickListener onPostClickListener;

    public PostRecyclerAdapter(List<PostModel> postModelList, OnPostClickListener onPostClickListener) {
        this.postModelList = postModelList;
        this.onPostClickListener = onPostClickListener;
    }

    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);


        return new ViewHolder(view, onPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ViewHolder holder, int position) {

        PostModel  pm = postModelList.get(position);

        String imageUrl;
        String profileUrl;

        holder.userName.setText(pm.getUserName());
        holder.title.setText(pm.getPostTitle());
        holder.description.setText(pm.getPostDescription());
     //   holder.timestamp.setText(pm.getTimestamp().toString());

        imageUrl = pm.getImageUrl();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_one)
                .fit()
                .into(holder.postImage);

        profileUrl = pm.getProfileUrl();

        if(profileUrl.isEmpty())
        {
            profileUrl = "https://firebasestorage.googleapis.com/v0/b/my-application-c2e07.appspot.com/o/post_images%2Fpost_image_1604052548?alt=media&token=41155647-5a94-4087-ae12-3246c68ad549";
        }
        Picasso.get()
                .load(profileUrl)
                .placeholder(R.drawable.image_one)
                .fit()
                .into(holder.profileImage);

//        holder.go_to_comment_post_row.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //go to next fragment to see the respective comments from the hash map
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView userName, title, description, timestamp, go_to_comment_post_row;
        ImageView postImage, profileImage;

        OnPostClickListener onPostClickListener;

        public ViewHolder(@NonNull View itemView, OnPostClickListener onPostClickListener) {
            super(itemView);

            userName = itemView.findViewById(R.id.username_post_row);
            title = itemView.findViewById(R.id.title_post_row);
            description = itemView.findViewById(R.id.description_post_row);
            timestamp = itemView.findViewById(R.id.timestamp_post_row);
            postImage = itemView.findViewById(R.id.image_post_row);
            profileImage = itemView.findViewById(R.id.profile_image_post_row);
            go_to_comment_post_row = itemView.findViewById(R.id.go_to_comment_post_row);

            this.onPostClickListener = onPostClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPostClickListener.onPostClick(getAdapterPosition());
        }
    }

    public interface OnPostClickListener
    {
        void onPostClick(int position);
    }
}