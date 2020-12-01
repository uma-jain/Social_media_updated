package fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Utils.CommentModel;
import Utils.PostModel;
import Utils.UserApi;
import ui.CommentRecyclerAdapter;

public class CommentsFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    UserApi userApi = UserApi.getInstance();

    private CollectionReference collectionReference = db.collection("post");



    private ProgressDialog progressDialog;




    private List<CommentModel> commentModelList = new ArrayList<CommentModel>();
    private RecyclerView recyclerView;
    private Button commentButton;
    private ImageView commentPostUserImg, commentPostImg;
    private TextView commentPostUserName, commentPostTitle, commentPostDescription, commentPostTimestamp;

    String docId = "";

    public CommentsFragment()
    {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_comment, container, false);

        //get hash map from bundle
        //convert the hash map into CommentModel
        //make arraylist of comment model
        //pass it to commentRecycleradapter
        assert view != null;
        recyclerView = view.findViewById(R.id.recycler_view_comment);
        commentButton = view.findViewById(R.id.comment_button);
        commentPostUserImg = view.findViewById(R.id.comment_user_image_post_row);
        commentPostImg = view.findViewById(R.id.comment_image_post_row);
        commentPostUserName = view.findViewById(R.id.comment_username_post_row);
        commentPostTitle = view.findViewById(R.id.comment_title_post_row);
        commentPostDescription = view.findViewById(R.id.comment_description_post_row);
        commentPostTimestamp = view.findViewById(R.id.comment_timestamp_post_row);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        assert getArguments() != null;
        final  PostModel pm= (PostModel) getArguments().getSerializable("postmodel");

        assert pm != null;
       // Toast.makeText(getContext(), pm.getPostTitle(), Toast.LENGTH_SHORT).show();

        //setting up the post details
        String imageUrl = pm.getProfileUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_one)
                .fit()
                .into(commentPostUserImg);

        String postImgUrl = pm.getImageUrl();
        Picasso.get()
                .load(postImgUrl)
                .placeholder(R.drawable.image_one)
                .fit()
                .into(commentPostImg);

        commentPostUserName.setText(pm.getUserName());
        commentPostTitle.setText(pm.getPostTitle());
        commentPostDescription.setText(pm.getPostDescription());
        commentPostTimestamp.setText(pm.getPostTime());


        refereshCommentsRecyclerView(pm);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog box
                //create dialog
                final Dialog dialog = new Dialog(getContext());
                //set content view
                dialog.setContentView(R.layout.dialog_add_comment);
                //Initialize width
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                //initialize height
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                //set layout
                dialog.getWindow().setLayout(width, height);
                //show dialog
                dialog.show();

                //initialize and assign variable
                final EditText commentText = dialog.findViewById(R.id.edit_text_dialog);
                Button btDone = dialog.findViewById(R.id.bt_done);


                btDone.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        //dismiss dialog
                        dialog.dismiss();
                        String comText = "";
                        //get updated text from edit text
                        if (!TextUtils.isEmpty(commentText.getText().toString().trim())) {
                            comText = commentText.getText().toString().trim();

                            String username = "";
                            if (TextUtils.isEmpty(userApi.getUsername())) {
                                username = "username";
                            } else {
                                username = userApi.getUsername();
                            }

                            //updateHashMapFirestore( username, comText);
                            updateCommentsFirestore(username, comText, pm);

                        } else {
                            Snackbar.make(Objects.requireNonNull(getView()), "Please Type some text", 1000);
                            Toast.makeText(getContext(), "Please Type some text", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        return view;
    }

    private void updateCommentsFirestore(final String username, final String comText, final PostModel pm)
    {

        collectionReference.whereEqualTo("postId", pm.getPostId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String did = document.getId();
                                CollectionReference collectionReference1 = db.collection("post").document(did).collection("comments");
                                CommentModel c = new CommentModel();
                                c.setUserName(username);
                                c.setComment(comText);
                                DateFormat dform = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                                Date obj = new Date();
                                String currTime = dform.format(obj).toString();
                                c.setCommentTimeStamp(currTime);
                                collectionReference1.add(c)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getContext(), "comment added successfully", Toast.LENGTH_SHORT).show();
                                                refereshCommentsRecyclerView(pm);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "failed to add", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    }
                });
    }

    private void refereshCommentsRecyclerView(PostModel pm) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Refreshing Comments...");
        progressDialog.show();

        commentModelList.clear();
        collectionReference.whereEqualTo("postId", pm.getPostId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String did = document.getId();
                                CollectionReference collectionReference2 = db.collection("post").document(did).collection("comments");
                                collectionReference2
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                                                    {
                                                        CommentModel cm = documentSnapshots.toObject(CommentModel.class);
                                                        commentModelList.add(cm);


                                                        CommentRecyclerAdapter commentRecyclerAdapter = new CommentRecyclerAdapter(commentModelList);
                                                        recyclerView.setAdapter(commentRecyclerAdapter);

                                                    }
                                                    if (commentModelList.isEmpty())
                                                    {
                                                        Toast.makeText(getContext(), "Be the first one to comment", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Be the first To comment", Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}
