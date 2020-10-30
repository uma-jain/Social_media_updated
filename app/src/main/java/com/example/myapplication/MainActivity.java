package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Utils.UserApi;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        frameLayout=findViewById(R.id.frameL);

        Toast.makeText(this, UserApi.getInstance().getEmail(), Toast.LENGTH_SHORT).show();


        //bydefault home
        Home home=new Home();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameL,new Home()).commit();
        //FILL FRAMELAYOUT BY FRAGMENT
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selected=null;
                switch (menuItem.getItemId()){
                    case R.id.Home:
                        selected=new Home();

                        break;
                    case R.id.Search:
                        selected=new SearchFragment();

                        break;
                    case R.id.Add:
                        selected=new AddFragment();

                        break;
                    case R.id.Notification:
                        selected=new NotificationFragment();

                        break;
                    case R.id.Profile:
                        selected=new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameL,selected).commit();
                return true;
            }
        });


        firebaseAuth=FirebaseAuth.getInstance();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if(currentUser == null ){
            startActivity(new Intent(MainActivity.this,login_signup_getstarted.class));
        }
    }

    @Override
    //inflate menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutbtn:
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,login_signup_getstarted.class));
        }
        return super.onOptionsItemSelected(item);
    }

}