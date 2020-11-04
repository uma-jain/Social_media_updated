package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Utils.UserApi;

public class login_signup_getstarted extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_getstarted);
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        //if user authenticated and profile is created go to main activity
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(currentUser != null ){
                    final String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("uid", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException error) {

                                    if (error != null)
                                    {
                                        return;
                                    }

                                    String currentUserName;
                                    if (!value.isEmpty())
                                    {
                                        for (QueryDocumentSnapshot snapshot:value)
                                        {
                                            UserApi userApi = UserApi.getInstance();
                                            userApi.setBio(snapshot.getString("bio"));
                                            userApi.setCover(snapshot.getString("cover"));
                                            userApi.setEmail(snapshot.getString("email"));
                                            userApi.setFollowerCount(snapshot.getString("follower"));
                                            userApi.setImage(snapshot.getString("image"));
                                            userApi.setPhone(snapshot.getString("phone"));
                                            userApi.setProfession(snapshot.getString("profession"));
                                            userApi.setUsername(snapshot.getString("username"));
                                            userApi.setUid(snapshot.getString("uid"));

                                            startActivity(new Intent(login_signup_getstarted.this,MainActivity.class));
                                            finish();
                                        }
                                    }

                                }
                            });
                }
                else
                {
                    //if he is new user
                }

            }
        };


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

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}