package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Personal_Chat_Activity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal__chat_);
        imageView=findViewById(R.id.civ_profilepic);
        textView=findViewById(R.id.tv_hisname);
        getSupportActionBar().hide();
    }
}