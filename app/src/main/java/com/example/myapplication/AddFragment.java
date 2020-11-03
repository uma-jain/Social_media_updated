package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Utils.PostModel;
import Utils.UserApi;


public class AddFragment extends Fragment  implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private EditText title, description;
    private ImageView imagePost;
    private Button savePost;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //connection to firestore database;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //here we are going to make another collection named Journal
    //and this collection reference is going to point to that journal
    private CollectionReference collectionReference = db.collection("post");


    //we need a storageReference for storing the images
    private StorageReference storageReference;

    private Uri imageUri;

    private UserApi userApi = UserApi.getInstance();


    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_add, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference =  FirebaseStorage.getInstance().getReference();

        title = view.findViewById(R.id.title_add_new_post);
        description = view.findViewById(R.id.description_add_new_post);
        imagePost = view.findViewById(R.id.image_add_new_post);
        savePost = view.findViewById(R.id.savePost);
        progressBar = view.findViewById(R.id.progress_bar_add);

        imagePost.setOnClickListener(this);
        savePost.setOnClickListener(this);

        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_add_new_post:
                //open gallery and get the image
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

            case R.id.savePost:
                //save the image in storage and then in post collection
                saveMyPost();
                break;
        }
    }

    private void saveMyPost() {
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(title.getText().toString().trim()) && !TextUtils.isEmpty(description.getText().toString().trim()) && imageUri != null)
        {
            final StorageReference filepath = storageReference
                    .child("post_images")
                    .child("post_image_"+ Timestamp.now().getSeconds());

            filepath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    //create postmodel object
//                                    private String uid, imageUrl, postTitle, postDescription, postId, timestamp, likes, userName, commentCnt, profileUrl;
//                                    private HashMap<String, String> comments;
                                    //in user api-- String email, uid,phone, image, bio, username, profession, cover, followerCount;
                                    PostModel postModel = new PostModel();
                                    postModel.setUid(userApi.getUid());
                                    postModel.setImageUrl(imageUrl);
                                    postModel.setPostTitle(title.getText().toString().trim());
                                    postModel.setPostDescription(description.getText().toString().trim());
                                    postModel.setPostId(UUID.randomUUID().toString());
                                    postModel.setTimestamp(Timestamp.now());
                                    postModel.setLikes("0");
                                    postModel.setUserName(userApi.getUsername());
                                    postModel.setCommentCnt("0");
                                    postModel.setProfileUrl(userApi.getImage());

                                    HashMap<String, String> commentsHm = new HashMap<String , String >();
                                    postModel.setComments((HashMap<String, String>) commentsHm);


                                    //send it to collection post
                                    collectionReference.add(postModel)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);

                                                    //go to home fragment
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "could not post right now", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE)
        {
            //this data is an image
            if (data != null)
            {
                //this imageUri is the path of the image in our android phone , here we get that path
                imageUri = data.getData();
                imagePost.setBackgroundResource(R.color.white);
                imagePost.setImageURI(imageUri);//show image

            }
        }

    }
}