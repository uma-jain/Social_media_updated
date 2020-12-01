package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;
import java.util.ArrayList;

import Utils.MessageModel;
import Utils.UserApi;
import Utils.UserModal;
import ui.MessageListAdapter;
import ui.UsersAdapter;

public class MessagesFragment extends Fragment {
    RecyclerView recyclerView;
    TextView textView;

    //foriebaseAuth
    FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    //firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    UserApi userApi = UserApi.getInstance();


    public MessagesFragment() {
        // Required empty public constructor
    }

    ArrayList<String> messagesModalList = new ArrayList<String>();
    MessageListAdapter messageListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmet_messages, container, false);
        //
        recyclerView = view.findViewById(R.id.fragment_messages_messageslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        textView = view.findViewById(R.id.nomessageyet);
        //getmessagelist;
        getMessageList();

        return view;
    }

    private void getMessageList() {
        //get access of messages arrayList of current user
        //Log.i("info","get messaages"+userApi.getUid());
        messagesModalList = userApi.getAl();
        for (int i = 0; i < messagesModalList.size(); i++)
            Log.i("info", messagesModalList.get(i) + " ");
        if (messagesModalList.size() > 0) {
            messageListAdapter = new MessageListAdapter(getActivity(), messagesModalList, MessagesFragment.this);
            recyclerView.setAdapter(messageListAdapter);
            textView.setVisibility(View.INVISIBLE);

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
        }

    }
}