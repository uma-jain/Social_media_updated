package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FirebaseUser currentuser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView tv_username,tv_bio,tv_prof;
    CircleImageView Profile_image;


    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
      }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        tv_username = view.findViewById(R.id.fp_username);
        tv_bio = view.findViewById(R.id.fp_bio);
        tv_prof = view.findViewById(R.id.fp_profession);
        Profile_image =(CircleImageView) view.findViewById(R.id.fp_profileImage);
        Query query=databaseReference.orderByChild("uid").equalTo(currentuser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot ds: snapshot.getChildren()) {
                    //check if length of name>1 else go to create profile activity and setup activity;
                    String name= (String) ds.child("username").getValue();
                    String bio= (String) ds.child("bio").getValue();
                    String profession= (String) ds.child("profesion").getValue();
                    String image= (String) ds.child("image").getValue();

                    if((name == null || name.length() == 0 )&&( bio==null || bio.length() ==0) &&( profession==null ||profession.length()==0)){
                        Toast.makeText(getContext(),"go to create profile"+name,Toast.LENGTH_LONG).show();
                        startActivity( new Intent(getActivity(),CreateProfile.class));
                    }

                    tv_bio.setText(bio);
                    tv_prof.setText(profession);
                    tv_username.setText(name);
                    Picasso.get().load("https://www.google.com/search?q=image&sxsrf=ALeKk03n9XokJdWZ8n40A3xQBFN2M5c0_A:1602241831745&tbm=isch&source=iu&ictx=1&fir=gxFxsvFBmxeZ9M%252C0JWe7yDOKrVFAM%252C%252Fm%252F0jg24&vet=1&usg=AI4_-kQBLby25ThXwL8EhAYi5U_DYa7tFg&sa=X&ved=2ahUKEwiFuub5r6fsAhXTjOYKHVEWCakQ_B16BAgDEAM#imgrc=gxFxsvFBmxeZ9M").into(Profile_image);
                    try{
                         Picasso.get().load(image).into(Profile_image);
                     }catch (Exception e){
                         Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(Profile_image);
                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}