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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Utils.PostModel;
import Utils.UserApi;
import Utils.UserApiModel;
import Utils.UserModal;
import ui.UsersAdapter;

public class SearchFragment extends Fragment implements UsersAdapter.OnUserClickListener {
    //foriebaseAuth
    FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
   //firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    RecyclerView recyclerView;
    UsersAdapter usersAdapter;
    private List<UserModal> userModalList = new ArrayList<UserModal>();

    private boolean flag = true;

    String concatUid="";

    UserApi userApi = UserApi.getInstance();

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
                               //if not current user then add
                                if(!userApi.getUid().equals(user.getUid())){
                                    userModalList.add(user);
                                }
                            }
                            //adapter
                            usersAdapter=new UsersAdapter(getActivity(),userModalList, SearchFragment.this);
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

                                if(user.getUsername().toLowerCase().contains(query.toLowerCase()) ){
                                    Log.i("info",user.getUsername());
                                    userModalList.add(user);
                                }
                            }
                            //adapter
                            usersAdapter=new UsersAdapter(getActivity(),userModalList,SearchFragment.this);
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

        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

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
            case R.id.todobtn:
                startActivity(new Intent(getActivity(), TodoActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserClick(int position) {
        Toast.makeText(getContext(), userModalList.get(position).getUid(), Toast.LENGTH_SHORT).show();
        updateHisFirestoreMessageArray(userModalList.get(position));
    }

    private void updateHisFirestoreMessageArray(final UserModal userModal) {


        collectionReference.whereEqualTo("uid",userModal.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> messageuids ;
                        if (task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                            {
                                UserApiModel userApiModel = documentSnapshots.toObject(UserApiModel.class);

                                messageuids = userApiModel.getMessageuids();
                                if (messageuids.contains(userApi.getUid()))
                                {
                                    Log.d("here3", "onUserClick: already chatted");
                                    Toast.makeText(getContext(), "You have Already Chatted With This user Check the chat list fragment", Toast.LENGTH_SHORT).show();
                                    sendUserModelAndDocRefId(userModal);
                                }
                                else
                                {
                                    Log.d("i am here", "onComplete: inside else");
                                        Log.d("i am here", "onComplete: inside if");

                                        //adding my uid to his array
                                        messageuids.add(userApi.getUid());
                                        for (String uids : messageuids)
                                        {
                                            Log.d("updated ids", "onComplete: "+uids);
                                        }
                                        //now again update the firestore
                                        HashMap<String, Object> messUidArrUpdated = new HashMap<String, Object>();
                                        messUidArrUpdated.put("messageuids", messageuids);
                                        String did = documentSnapshots.getId();
                                        final DocumentReference docRef = db.collection("Users").document(did);

                                        docRef.update(messUidArrUpdated)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "his Array updated in firestore", Toast.LENGTH_SHORT).show();
                                                        updateMyFirestoreMessageArray(userModal);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to update array", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                }
                                break;
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void updateMyFirestoreMessageArray(final UserModal userModal) {

        collectionReference.whereEqualTo("uid", userApi.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final ArrayList<String> myuids;
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                            {
                                final UserApiModel userApiModel = documentSnapshot.toObject(UserApiModel.class);
                               myuids = userApiModel.getMessageuids();

                                //add his uid
                                myuids.add(userModal.getUid());

                                //now update the firestore
                                HashMap<String, Object> messUidArrUpdated = new HashMap<String, Object>();
                                messUidArrUpdated.put("messageuids", myuids);
                                String did = documentSnapshot.getId();
                                final DocumentReference docRef = db.collection("Users").document(did);

                                docRef.update(messUidArrUpdated)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "my Array updated in firestore", Toast.LENGTH_SHORT).show();

                                                //update locally
                                                userApi.setAl(myuids);
                                                //make the document of message for these two users and then open the next activity
                                                makeMessDocAndStart(userModal);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Failed to update array", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            }


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void makeMessDocAndStart(final UserModal userModal)
    {
        final CollectionReference collectionReference2 = db.collection("messages");

        String u1 = userModal.getUid();
        String u2 = userApi.getUid();
        int res = stringCompare(u1, u2);

        if(res<0)
        {
            concatUid = u1+"_"+u2;
        }
        else
        {
            concatUid = u2+"_"+u1;
        }

        HashMap<String, Object> uidArrayHm = new HashMap<String, Object>();
        uidArrayHm.put("specialUid",concatUid);

        collectionReference2.add(uidArrayHm)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        final String docId = documentReference.getId();
                        CollectionReference collectionReference3 = collectionReference2.document(docId).collection("messagesInfo");

                        HashMap<String, Object> messageHmModels = new HashMap<String, Object>();
                        messageHmModels.put("messageText", "Hi");
                        messageHmModels.put("sendBy", userModal.getUid());
                        DateFormat dform = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                        Date obj = new Date();
                        String currTime = dform.format(obj).toString();
                        messageHmModels.put("messageTime", currTime);

                        collectionReference3.add(messageHmModels)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("userModel",userModal);
                                        bundle.putString("documentId", docId);
                                        Log.d("here6", "onUserClick:");
                                        Intent intent = new Intent(getContext(), Personal_Chat_Activity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void sendUserModelAndDocRefId(final UserModal userModal) {

        String u1 = userModal.getUid();
        String u2 = userApi.getUid();

        int res= stringCompare(u1, u2);
        if(res<0)
        {
            concatUid = u1+"_"+u2;
        }
        else
        {
            concatUid = u2+"_"+u1;
        }
        

        CollectionReference collectionReference5 = db.collection("messages");
        collectionReference5
               .whereEqualTo("specialUid",concatUid)
                .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                        for (QueryDocumentSnapshot document: task.getResult())
                        {
                            String did = document.getId();
                            final DocumentReference docRef = db.collection("post").document(did);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userModel",userModal);
                            bundle.putString("documentId", did);
                            Log.d("here6", "onUserClick:");
                            Intent intent = new Intent(getContext(), Personal_Chat_Activity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("exception message", "onFailure: " + e.getMessage());
            }
        });
    }

    public int stringCompare(String str1, String str2)
    {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        if (l1 != l2) {
            return l1 - l2;
        }

        else {
            return 0;
        }
    }
}