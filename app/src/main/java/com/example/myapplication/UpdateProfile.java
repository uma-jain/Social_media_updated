package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;

    //views
    ImageView addcover,addprofile,cover_container,profile_container;
    TextView nameEt,bioEt,ProffEt;
    Button updateProfile;

    Uri profileUri=null;
    Uri coverUri=null;
    private static final int PICK_IMAGE=1;
    String profileOrCover;
    UploadTask uploadTask;

    //realtime databse
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firebaseAuth =FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        //storage
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_cover");
        //tv and iv
        addcover=(ImageView) findViewById(R.id.up_addcover);
        addprofile=(ImageView) findViewById(R.id.up_addprofile);
        cover_container=findViewById(R.id.up_cover);
        profile_container=findViewById(R.id.up_profileImage);
        nameEt=findViewById(R.id.up_username);
        bioEt=findViewById(R.id.up_bio);
        ProffEt=findViewById(R.id.up_profession);

        updateProfile=findViewById(R.id.up_UpdateProfile);

        getSupportActionBar().setTitle("Update Profile");
        //usr null go to start
        if(currentUser == null){
            startActivity(new Intent(UpdateProfile.this,MainActivity.class));
        }
        //set existing data
        Query query=databaseReference.orderByChild("uid").equalTo(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //check if length of name>1 else go to create profile activity and setup activity;
                    String name = (String) ds.child("username").getValue();
                    String bio = (String) ds.child("bio").getValue();
                    String profession = (String) ds.child("profesion").getValue();
                    String image =  ds.child("image").getValue(String.class);
                    String cover =  ds.child("cover").getValue(String.class);


                    if ((name == null || name.length() == 0) && (bio == null || bio.length() == 0) && (profession == null || profession.length() == 0)) {
                        Toast.makeText(UpdateProfile.this, "go to create profile" + name, Toast.LENGTH_LONG).show();
                       // startActivity(new Intent(UpdateProfile.this, CreateProfile.class));
                    }
                    bioEt.setText(bio);
                    ProffEt.setText(profession);
                    nameEt.setText(name);
                    try {
                        Log.i("info", image);
                        Picasso.get().load(image).into(profile_container);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(profile_container);
                    }
                    try {
                        Picasso.get().load(cover).into(cover_container);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(cover_container);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set onclick listerners and store data;




        }
}