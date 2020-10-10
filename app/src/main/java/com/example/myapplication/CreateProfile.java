package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateProfile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
      //  getSupportActionBar().setBackgroundDrawable(
      //          new ColorDrawable(Color.parseColor("#ffffff")));

        getSupportActionBar().setTitle("Create Profile");
        
        if(currentUser == null){
            startActivity(new Intent(CreateProfile.this,MainActivity.class));
        }
    }
}