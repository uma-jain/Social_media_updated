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

                                if(user.getUsername().toLowerCase().contains(query.toLowerCase())){
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

    @Override
    public void onUserClick(int position) {

        Toast.makeText(getContext(), userModalList.get(position).getUid(), Toast.LENGTH_SHORT).show();
                //go to mainactivity2


        //add uid in userapi to messageuids arr of model present at position i
        Log.d("here-1", "onUserClick: ");
        boolean res = updateHisFirestoreMessageArray(userModalList.get(position));
        Log.d("here-2", "onUserClick: ");

        if (res)
        {
            Log.d("here4", "onUserClick: calling my update");
//            updateMyFirestoreMessageArray(userModalList.get(position));
            Log.d("here5", "onUserClick: called my update");

//            Bundle bundle = new Bundle();
//            UserModal userModal = userModalList.get(position);
//            bundle.putSerializable("userModel",userModal);
//            Log.d("here6", "onUserClick:");
//            Intent intent = new Intent(getContext(), Personal_Chat_Activity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
            Log.d("here7", "onUserClick:");

        }
        else
        {
            Toast.makeText(getContext(), "You have Already Chatted With This user Check the chat list fragment", Toast.LENGTH_SHORT).show();
        }



        //while getting this bundle in chatactivity
//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();
//
//        List<Thumbnail> thumbs=
//                (List<Thumbnail>)bundle.getSerializable("value");

//
//              Intent intent = new Intent(context, ChatActivity.class);
//              intent.putExtra("uid", userModelList.get(i).getUid());
//              context.startActivity(intent);
    }

    private void updateMyFirestoreMessageArray(final UserModal userModal) {
        final ArrayList<String> messageuids;
        //first update locally
        UserApi userApi = UserApi.getInstance();
        messageuids = userApi.getAl();
        messageuids.add(userModal.getUid());

        //now update in firestore
        collectionReference.whereEqualTo("uid", userApi.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                            {
                                final UserApiModel userApiModel = documentSnapshot.toObject(UserApiModel.class);

                                //now update the firestore
                                HashMap<String, Object> messUidArrUpdated = new HashMap<String, Object>();
                                messUidArrUpdated.put("messageuids", messageuids);
                                String did = documentSnapshot.getId();
                                final DocumentReference docRef = db.collection("Users").document(did);

                                docRef.update(messUidArrUpdated)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "my Array updated in firestore", Toast.LENGTH_SHORT).show();

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

    String sortString(String inputString)
    {
        char tempArray[] = inputString.toCharArray();

        // sort tempArray
        Arrays.sort(tempArray);

        // return new sorted string
        return new String(tempArray);
    }

    private void makeMessDocAndStart(final UserModal userModal)
    {
        final CollectionReference collectionReference2 = db.collection("messages");
        //CollectionReference collectionReference3 = collectionReference2.document().collection("messagesText");
        //put an array of the two users
//        ArrayList<String> uids = new ArrayList<String>();
//        uids.add(UserApi.getInstance().getUid());
//        uids.add(userModal.getUid());

        //AlgRTJNSbuYKSYAYYbtnRj5HQEs1 EVzE9fdo7DMpQc0T5SfzVh2lDoS2
        //01225579AADDEEEHJKMNQQRRSSSSTTVVYYYYbbcdffghjllnoopstuzz

        //LFWksF6p6xWHZRsDLLfRb9gLFsv2 AlgRTJNSbuYKSYAYYbtnRj5HQEs1
        //125669AADEFFFHHJKLLLLNQRRRRSSTWWYYYYZbbbfggjklnpsssstuvx
        String concatUid = UserApi.getInstance().getUid() +" "+ userModal.getUid();
        concatUid = sortString(concatUid);

        HashMap<String, Object> uidArrayHm = new HashMap<String, Object>();
        uidArrayHm.put("specialUid",concatUid);


        final String finalConcatUid = concatUid;
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

                                        //send document id and the userModel to chat activity
                                        Bundle bundle = new Bundle();
                                        //UserModal userModal = userModalList.get(position);
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

        //make a message collection


    }

    private boolean updateHisFirestoreMessageArray(final UserModal userModal) {
        //get the current array of messageuids from firestore
        final ArrayList<String> messageuids = new ArrayList<String>();

        collectionReference.whereEqualTo("uid",userModal.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshots: task.getResult())
                            {
                                UserApiModel userApiModel = documentSnapshots.toObject(UserApiModel.class);
                                if(userApiModel.getMessageuids().size() !=0 )
                                {
                                    for(String uid : userApiModel.getMessageuids())
                                    {
                                        if (!uid.equals(UserApi.getInstance().getUid()))
                                        {
                                            Log.d("here-2", "onUserClick: ");

                                            Log.d("his message ids", "onComplete: "+uid);
                                            messageuids.add(uid);

                                        }
                                        else
                                        {
                                            flag = false;
                                            Log.d("here3", "onUserClick: already chatted");
                                            Toast.makeText(getContext(), "You have Already Chatted With This user Check the chat list fragment", Toast.LENGTH_SHORT).show();

                                            sendUserModelAndDocRefId(userModal);

                                        }
                                    }
                                }

                                else
                                {
                                    if (flag == true)
                                    {
                                        //adding my uid to his array
                                        messageuids.add(UserApi.getInstance().getUid());

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
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Failed to update array", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
//                                        break;
                                    }
                                    updateMyFirestoreMessageArray(userModal);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        return flag;
        //update the array
        //update firestore


    }

    private void sendUserModelAndDocRefId(final UserModal userModal) {
        //find the appropriate model and then send it to chat activity

        final String specialUid = sortString(userModal.getUid() + " " + UserApi.getInstance().getUid());

        CollectionReference collectionReference5 = db.collection("messages");
        collectionReference5
               .whereEqualTo("specialUid",specialUid)
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

                            //send the document id and the userModel to next activity
                            //send document id and the userModel to chat activity
                            Bundle bundle = new Bundle();
                            //UserModal userModal = userModalList.get(position);
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

            }
        });
    }
}