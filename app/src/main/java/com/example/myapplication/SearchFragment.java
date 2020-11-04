package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import Utils.UserModal;
import ui.UsersAdapter;

public class SearchFragment extends Fragment {
    //foriebaseAuth
    FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
   //firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    private List<UserModal> userModalList = new ArrayList<UserModal>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         currentUser=FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView=view.findViewById(R.id.f_search_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //getAll users
        getAllUsers();
        return view;

    }
    private void getAllUsers() {
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {   userModalList = new ArrayList<UserModal>();
                            for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                            {
                                UserModal user = documentSnapshots.toObject(UserModal.class);
                               // Log.i("info",user.getEmail());
                                userModalList.add(user);
                            }
                            //adapter
                            usersAdapter=new UsersAdapter(getActivity(),userModalList);
                            recyclerView.setAdapter(usersAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void searchUsers(final String query)
    {  //get searched results
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            userModalList = new ArrayList<UserModal>();
                            for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                            {
                                UserModal user = documentSnapshots.toObject(UserModal.class);
                                // Log.i("info",user.getEmail());

                                if(user.getUsername().toLowerCase().contains(query.toLowerCase())){
                                    Log.i("info",user.getUsername());
                                    userModalList.add(user);
                                }
                            }
                            //adapter
                            usersAdapter=new UsersAdapter(getActivity(),userModalList);
                            //refresh adapter
                            usersAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(usersAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void onCreate(Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    //inflate menu
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem menuItem = menu.findItem(R.id.search_btn);
        menuItem.setVisible(true);
        firebaseAuth=FirebaseAuth.getInstance();

        //SaerchView
       // MenuItem menuItem=menu.findItem(R.id.search_btn);

//        MenuItem search = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) search.getActionView();

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user clicks on btn
                //if search query not empty
                if(!TextUtils.isEmpty(s.trim())){
                  //s contains name of user
                    Log.i("info","get users with name this");
                    searchUsers(s);

                }else{
                  getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //user enter letter
                if(!TextUtils.isEmpty(s.trim())){
                    //s contains name of user
                    searchUsers(s);
                }else{
                    getAllUsers();
                }

                return false;
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutbtn:
             firebaseAuth.signOut();
                startActivity(new Intent(getActivity(),login_signup_getstarted.class));
                getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

}