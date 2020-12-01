package ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import java.util.List;

import Utils.CommentModel;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>
{
    List<CommentModel> commentModelList;
    public CommentRecyclerAdapter(List<CommentModel> commentModelList)
    {
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerAdapter.ViewHolder holder, int position) {

        CommentModel cm = commentModelList.get(position);
        holder.userNameComment.setText(cm.getUserName());
        holder.commentTextC.setText(cm.getComment());
        holder.commentTime.setText(cm.getCommentTimeStamp());
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userNameComment;
        public TextView commentTextC;
        public TextView commentTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           userNameComment = itemView.findViewById(R.id.comment_user_name);
            commentTextC = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }
}
