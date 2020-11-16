package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import Utils.MessageModel;
import Utils.UserApi;
import Utils.UserModal;

public class Personal_Chat_Activity extends AppCompatActivity {
      Toolbar toolbar;
//    RecyclerView recyclerView;
      TextView hisStatus, hisName;
      ImageView profilePic;
      EditText chatMessage;
      Button sendButton;
//
      private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
      private FirebaseFirestore db = FirebaseFirestore.getInstance();
      private CollectionReference collectionReference;


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

//        recyclerView = findViewById(R.id.recylerview_message_list);
          profilePic = findViewById(R.id.civ_profilepic);
          hisName = findViewById(R.id.tv_hisname);
          hisStatus = findViewById(R.id.tv_his_status);
          chatMessage = findViewById(R.id.edittext_chatbox);
          sendButton = findViewById(R.id.button_chatbox_send);

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
