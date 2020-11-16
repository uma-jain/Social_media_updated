package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Utils.PostModel;
import Utils.UserApi;
import fragments.CommentsFragment;
import ui.PostRecyclerAdapter;

public class Home extends Fragment implements PostRecyclerAdapter.OnPostClickListener {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<PostModel> postModelList = new ArrayList<PostModel>();

    private PostRecyclerAdapter postRecyclerAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    UserApi userApi = UserApi.getInstance();

    private CollectionReference collectionReference = db.collection("post");


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.home_progress_bar);
        UserApi userApi = UserApi.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //get data from post collection
        getDataFromFirestore();
        //send it to recycler view



        return view;
    }
    public void onCreate(Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    //inflate menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem menuItem = menu.findItem(R.id.search_btn);
        menuItem.setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutbtn:
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(),login_signup_getstarted.class));
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void getDataFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                            {
                                PostModel p = documentSnapshots.toObject(PostModel.class);
                                postModelList.add(p);
                            }

                            postRecyclerAdapter = new PostRecyclerAdapter(postModelList, Home.this);
                            recyclerView.setAdapter(postRecyclerAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onPostClick(int position) {
        Toast.makeText(getContext(), "psotion"+position, Toast.LENGTH_SHORT).show();
        Bundle b = new Bundle();
        PostModel p = postModelList.get(position);
        b.putSerializable("postmodel", p);
//        b.putSerializable("hashMap", postModelList.get(position).getComments());

        CommentsFragment cf = new CommentsFragment();
        cf.setArguments(b);

        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction= Objects.requireNonNull(fragmentManager).beginTransaction();
        fragmentTransaction.replace(R.id.frame_home,cf).addToBackStack(null).commit();
    }

}