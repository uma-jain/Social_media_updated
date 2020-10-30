package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    CircleImageView profile_image_container;
    ImageView coverImageContainer;
    FloatingActionButton editProfileBtn;
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

        profile_image_container =(CircleImageView) view.findViewById(R.id.fp_profileImage);
        coverImageContainer=view.findViewById(R.id.fp_cover);
        editProfileBtn=view.findViewById(R.id.fp_editprofile);

        Query query=databaseReference.orderByChild("uid").equalTo(currentuser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot ds: snapshot.getChildren()) {
                    //check if length of name>1 else go to create profile activity and setup activity;
                    String name= (String) ds.child("username").getValue();
                    String bio= (String) ds.child("bio").getValue();
                    String profession= (String) ds.child("profesion").getValue();
                    String image=  ds.child("image").getValue(String.class);
                    String cover=  ds.child("cover").getValue(String.class);

                    if((name == null || name.length() == 0 )&&( bio==null || bio.length() ==0) &&( profession==null ||profession.length()==0)){
                        Toast.makeText(getContext(),"go to create profile"+name,Toast.LENGTH_LONG).show();
                        startActivity( new Intent(getActivity(),CreateProfile.class));
                    }
                    tv_bio.setText(bio);
                    tv_prof.setText(profession);
                    tv_username.setText(name);
                     try{
                         Log.i("info",image);
                         Picasso.get().load(image).into(profile_image_container);
                     }catch (Exception e){
                         Log.i("exception image",image);
                         Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(profile_image_container);
                     }
                    try{
                        Log.i("info",cover);
                        Picasso.get().load(cover).into(coverImageContainer);
                    }catch (Exception e){
                        Log.i("excepton image",cover);
                        Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(coverImageContainer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),UpdateProfile.class));
            }
        });
        return view;
    }
}