
package ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Utils.PostModel;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.MyViewHolder> {

    ArrayList personImages;

    Context context;
    public ImageGridAdapter(Context context, ArrayList personImages) {
        this.context = context;
        this.personImages = personImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final PostModel pm = (PostModel) personImages.get(position);
        
        String imageUrl;
        imageUrl = pm.getImageUrl();

        try {
            Glide.with(context).load(imageUrl).into(holder.post_image);

        } catch (Exception e) {
            Glide.with(context).load(R.drawable.logo).into(holder.post_image);
        }
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                Toast.makeText(context,pm.getPostTitle(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return personImages.size();
    }
     class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView post_image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            post_image =  itemView.findViewById(R.id.post_image);
        }
    }
}