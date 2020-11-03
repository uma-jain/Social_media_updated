package ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Utils.PostModel;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    private List<PostModel> postModelList;

    public PostRecyclerAdapter(List<PostModel> postModelList) {
        this.postModelList = postModelList;
    }

    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ViewHolder holder, int position) {

        PostModel  pm = postModelList.get(position);

        String imageUrl;

        holder.userName.setText(pm.getUserName());
        holder.title.setText(pm.getPostTitle());
        holder.description.setText(pm.getPostDescription());
        holder.timestamp.setText(pm.getTimestamp());

        imageUrl = pm.getImageUrl();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_one)
                .fit()
                .into(holder.postImage);

    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, title, description, timestamp;
        ImageView postImage;
        ImageButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username_post_row);
            title = itemView.findViewById(R.id.title_post_row);
            description = itemView.findViewById(R.id.description_post_row);
            timestamp = itemView.findViewById(R.id.timestamp_post_row);
            postImage = itemView.findViewById(R.id.image_post_row);
            likeButton = itemView.findViewById(R.id.like_post_row);
        }
    }
}
