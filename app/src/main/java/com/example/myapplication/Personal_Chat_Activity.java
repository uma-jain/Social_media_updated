package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Personal_Chat_Activity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView tv_his_status, tv_hisname;
    ImageView civ_profilepic;
    EditText edittext_chatbox;
    ImageButton button_chatbox_send;

    FirebaseAuth firebaseAuth;
    String hisUid;
    String myUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_personal__chat_);
        recyclerView = findViewById(R.id.recylerview_message_list);
        civ_profilepic = findViewById(R.id.civ_profilepic);
        tv_hisname = findViewById(R.id.tv_hisname);
        tv_his_status = findViewById(R.id.tv_his_status);


        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");



        firebaseAuth = firebaseAuth.getInstance();





    }

    private void checkUserStatus() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            myUid=currentUser.getUid();

        } else {
            startActivity(new Intent(this, login_signup_getstarted.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
       // menu.findItem(R.id.action_search).setVisible=(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logoutbtn){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
