package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_signup_getstarted extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();

        //if user authenticated and profile is created go to main activity
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser != null ){
               startActivity(new Intent(login_signup_getstarted.this,MainActivity.class));
               finish();
        }

        setContentView(R.layout.activity_login_signup_getstarted);
        Button btnsignup=findViewById(R.id.start_signup_btn);
        Button btnlogin=findViewById(R.id.start_login_btn);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(login_signup_getstarted.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(login_signup_getstarted.this,Signup.class);
                startActivity(intent);
                finish();
            }
        });
    }
}