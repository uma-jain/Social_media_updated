package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CreateProfile extends AppCompatActivity {
    //realtime datanabase
    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;

    //views
    ImageView addcover,addprofile,cover_container,profile_container;
    TextView nameEt,bioEt,ProffEt;
    Button  createProfile;

    Uri profileUri=null;
    Uri coverUri=null;
    private static final int PICK_IMAGE=1;
    String profileOrCover;
    UploadTask uploadTask;

    //realtime databse
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //current user
        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        //storage
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_cover");
        //tv and iv
        addcover=(ImageView) findViewById(R.id.cp_addcover);
        addprofile=(ImageView) findViewById(R.id.cp_addprofile);
        cover_container=findViewById(R.id.cp_cover);
        profile_container=findViewById(R.id.cp_profileImage);
        nameEt=findViewById(R.id.cp_username);
        bioEt=findViewById(R.id.cp_bio);
        ProffEt=findViewById(R.id.cp_profession);

        createProfile=findViewById(R.id.cp_createProfile);

        getSupportActionBar().setTitle("Create Profile");
        //usr null go to start
        if(currentUser == null){
            startActivity(new Intent(CreateProfile.this,MainActivity.class));
        }
        //add cover photo
        addcover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCover="cover";
                Toast.makeText(CreateProfile.this,"add cover",Toast.LENGTH_LONG).show();
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        //add profile photo
        addprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCover="profile";
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);
                Toast.makeText(CreateProfile.this,"add cover",Toast.LENGTH_LONG).show();
            }
        });
        //add data
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              uploadData();
            }
        });

    }
    private void uploadData() {
        final String bio=bioEt.getText().toString();
        final String username=nameEt.getText().toString();
        final String prof=ProffEt.getText().toString();
        //add data is profile
        final HashMap<String,Object> profile=new HashMap<>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Profile...It will take a while....");

        //check if all fields are filled
        if(!TextUtils.isEmpty(bio) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(prof) ||  String.valueOf(profileUri) !=null || String.valueOf(coverUri) !=null) {
            progressDialog.show();
            //upload both images to storage
            final StorageReference reference=mStorageRef.child(System.currentTimeMillis()+"."+getFileExt(profileUri));
            final StorageReference reference1=mStorageRef.child(System.currentTimeMillis()+"."+getFileExt(coverUri));
            //profile image
            uploadTask=reference.putFile(profileUri);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    Log.i("info","inside upload profile");
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    Log.i("info","inside upload profile");
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                   Uri profiledownloadUri=task.getResult();
                    Log.i("info","add upload profile in hashmap");
                    profile.put("image",profiledownloadUri.toString());
                }
            });
            //add cover image
            uploadTask=reference1.putFile(coverUri);
            Task<Uri> uriTask1=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    Log.i("info","inside upload cover");
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    Log.i("info","inside upload cover");
                    return reference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                //save to realtimedtaabase
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //add va;ues in hashmap
                    Log.i("info","inside upload data");
                    Uri coveruri=task.getResult();
                    profile.put("cover",coveruri.toString());
                    profile.put("username",username);
                    profile.put("bio",bio);
                    profile.put("profesion",prof);

                    //get user
                    firebaseAuth =FirebaseAuth.getInstance();
                    FirebaseUser currentUser=firebaseAuth.getCurrentUser();

                    databaseReference.child(currentUser.getUid()).updateChildren(profile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateProfile.this,"PROFILE Created Successfully",Toast.LENGTH_LONG).show();
                            //redirect to profile fragment
                            Intent intent =new Intent(CreateProfile.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateProfile.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                }
            });


        }
        else{

            Toast.makeText(CreateProfile.this,"PLEASE FILL ALL FIELDS",Toast.LENGTH_SHORT).show();
        }



        }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){

                if( profileOrCover=="profile"){
                    profileUri=data.getData();
                    Picasso.get().load(profileUri).into(profile_container);
                }else {
                    coverUri=data.getData();
                    Picasso.get().load(coverUri).into(cover_container);
                }

            }
        }catch (Exception e){
            Toast.makeText(CreateProfile.this,"Error",Toast.LENGTH_SHORT).show();
        }
    }
    //for image storage
    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}