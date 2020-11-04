package fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Utils.CommentModel;
import Utils.PostModel;
import Utils.UserApi;
import ui.CommentRecyclerAdapter;
import ui.PostRecyclerAdapter;

public class CommentsFragment extends Fragment {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    UserApi userApi = UserApi.getInstance();

    private CollectionReference collectionReference = db.collection("post");







    private List<CommentModel> commentModelList = new ArrayList<CommentModel>();
    private RecyclerView recyclerView;
    private Button commentButton;


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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        assert getArguments() != null;
        final  PostModel pm= (PostModel) getArguments().getSerializable("postmodel");

        assert pm != null;
        Toast.makeText(getContext(), pm.getPostTitle(), Toast.LENGTH_SHORT).show();

        final HashMap<String, String> commentsHm = pm.getComments();

        for(Map.Entry<String, String> entry: commentsHm.entrySet())
        {
            CommentModel cm = new CommentModel();
            cm.setUserName(entry.getKey());
            cm.setComment(entry.getValue());
            commentModelList.add(cm);
//            System.out.println("Size = "+ entry.getKey()+"  qty = "+entry.getValue());
        }

        for(int i=0; i<commentModelList.size(); i++)
        {
            Log.d("comment username", "onCreateView: "+commentModelList.get(i).getUserName());
        }

        //send to recycler view
        CommentRecyclerAdapter commentRecyclerAdapter = new CommentRecyclerAdapter(commentModelList);
        recyclerView.setAdapter(commentRecyclerAdapter);


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
                        if (!TextUtils.isEmpty(commentText.getText().toString().trim()))
                        {
                            comText = commentText.getText().toString().trim();
                            updateHashMapFirestore(comText, userApi.getUsername());
                        }
                        else {
                            Snackbar.make(Objects.requireNonNull(getView()), "Please Type some text", 1000);
                            Toast.makeText(getContext(), "Please Type some text", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void updateHashMapFirestore(String comText, String username) {
                if(username.equals(""))
                {
                    username = "username";
                }
                commentsHm.put(username, comText);

                final HashMap<String, Object> commentsUpdated = new HashMap<String, Object>();
                commentsUpdated.put("comments", commentsHm);


                    CommentModel cm = new CommentModel(username, comText);
                    commentModelList.add(cm);
                CommentRecyclerAdapter commentRecyclerAdapter = new CommentRecyclerAdapter(commentModelList);
                recyclerView.setAdapter(commentRecyclerAdapter);

//                //now upadate hashmap in firestore
//                final Map<String, Object> commentsHmObj = new HashMap<String, Object>();
//
//                commentsHmObj.put("comments",commentsUpdated);
//
                collectionReference.whereEqualTo("postId",pm.getPostId()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot document: task.getResult())
                                    {
                                        String did = document.getId();
                                        Log.d("doc id", "onComplete: "+did);
                                        Log.d("post id", "onComplete: "+pm.getPostId());
                                        final DocumentReference docRef = db.collection("post").document(did);
                                        
                                        docRef.update(commentsUpdated)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Comments updated in firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        break;
                                    }
                                }
                            }
                        });
                //then refresh the list
            }
        });


        return view;
    }
}
