package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Utils.MessageModel;
import Utils.UserApi;
import Utils.UserModal;
import ui.MessagesAdapter;
import ui.UsersAdapter;

public class Personal_Chat_Activity extends AppCompatActivity {
      Toolbar toolbar;
      TextView hisStatus, hisName;
      ImageView profilePic;
      EditText chatMessage;
    private ProgressDialog progressDialog;
      Button sendButton;
//
      private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      private FirebaseFirestore db = FirebaseFirestore.getInstance();
      private CollectionReference collectionReference;
      //Handler mHandler;


    private List<MessageModel> messagesList = new ArrayList<MessageModel>();
      MessagesAdapter messagesAdapter;
       RecyclerView recyclerView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_personal__chat_);

        //here get the userModel from the bundle and also the document id of the message collection for these users
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        UserModal userModal = (UserModal) bundle.getSerializable("userModel");
        String documentId = bundle.getString("documentId");
        Toast.makeText(this, documentId, Toast.LENGTH_SHORT).show();
        collectionReference = db.collection("messages").document(documentId).collection("messagesInfo");

         recyclerView = findViewById(R.id.recylerview_message_list);
          profilePic = findViewById(R.id.civ_profilepic);
          hisName = findViewById(R.id.tv_hisname);
          hisStatus = findViewById(R.id.tv_his_status);
          chatMessage = findViewById(R.id.edittext_chatbox);
          sendButton = findViewById(R.id.button_chatbox_send);
    //initiate recycle view
                recyclerView.setHasFixedSize(true);
               recyclerView.setLayoutManager(new LinearLayoutManager(this));

          initmessageList();
          //set adapter
        //sort messageList
        //adapter
        messagesAdapter=new MessagesAdapter(getApplicationContext(),messagesList);
        recyclerView.setAdapter(messagesAdapter);
          sendButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (!TextUtils.isEmpty(chatMessage.getText().toString().trim()))
                  {
                      //if not empty then send to firestore

                      MessageModel messageModel = new MessageModel();
                      messageModel.setMessageText(chatMessage.getText().toString().trim());
                      DateFormat dform = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                      Date obj = new Date();
                      String currTime = dform.format(obj);
                      messageModel.setMessageTime(currTime);
                      messageModel.setSendBy(UserApi.getInstance().getUid());

                      HashMap<String, Object> messageHm = new HashMap<String, Object>();
                      messageHm.put("messageText", messageModel.getMessageText());
                      messageHm.put("messageTime", messageModel.getMessageTime());
                      messageHm.put("sendBy", messageModel.getSendBy());

                      collectionReference.add(messageHm)
                              .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                  @Override
                                  public void onComplete(@NonNull Task<DocumentReference> task) {
                                      Toast.makeText(Personal_Chat_Activity.this, "", Toast.LENGTH_SHORT).show();
                                      chatMessage.setText("");
                                      initmessageList();
                                  }
                              })
                              .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Toast.makeText(Personal_Chat_Activity.this, "Sorry try again later server down", Toast.LENGTH_SHORT).show();
                                  }
                              });
                  }
              }
          });

          if (userModal.getUsername().equals(""))
          {
              hisName.setText("Unknown");
          }
          else
          {
              hisName.setText(userModal.getUsername());
          }
          if (!userModal.getImage().equals(""))
          {
              Picasso.get()
                      .load(userModal.getImage())
                      .placeholder(R.drawable.image_one)
                      .fit()
                      .into(profilePic);
          }
//        this.mHandler = new Handler();
//        m_Runnable.run();
    }

//    private final Runnable m_Runnable = new Runnable()
//    {
//        public void run()
//
//        {
//            Toast.makeText(Personal_Chat_Activity.this,"in runnable",Toast.LENGTH_SHORT).show();
//
//            Personal_Chat_Activity.this.mHandler.postDelayed(m_Runnable,2000);
//
//            Toast.makeText(Personal_Chat_Activity.this, "run refresh", Toast.LENGTH_SHORT).show();
//        }
//
//    };

    private void initmessageList() {
        //get data set adapter;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking current status.. Please wait");
        progressDialog.show();
        collectionReference.orderBy("messageTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                messagesList.clear();

                for (QueryDocumentSnapshot doc : value) {
                    MessageModel msg = doc.toObject(MessageModel.class);
                    // Log.i("info",user.getEmail());
                    messagesList.add(msg);
                }
                Log.i("info","data set changed");
                messagesAdapter.notifyDataSetChanged();
            }

        });

        progressDialog.dismiss();
    }

//    private void initmessageList2() {
//        //get data set adapter;
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Refreshing...");
//        progressDialog.show();
//        collectionReference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        messagesList.clear();
//                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                            MessageModel msg = doc.toObject(MessageModel.class);
//                            // Log.i("info",user.getEmail());
//                            messagesList.add(msg);
//                        }
//
//                        //sort message based on datetime
//                        Collections.sort(messagesList, new Comparator<MessageModel>() {
//                            DateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
//                            @Override
//                            public int compare(MessageModel o1, MessageModel o2) {
//                                try {
//                                    return f.parse(o1.getMessageTime()).compareTo(f.parse(o2.getMessageTime()));
//                                } catch (Exception e) {
//                                    throw new IllegalArgumentException(e);
//                                }
//                            }
//                        });
//
//                        Log.i("info","data set changed");
//                        messagesAdapter.notifyDataSetChanged();
//                        progressDialog.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.search_btn).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logoutbtn){
            firebaseAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
