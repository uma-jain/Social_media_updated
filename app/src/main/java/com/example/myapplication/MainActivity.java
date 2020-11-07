package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import Utils.UserApi;
import fragments.CreateProfileFragment;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();
        //checkUserStatus();
        bottomNavigationView=findViewById(R.id.bottom_nav);
        frameLayout=findViewById(R.id.frameL);

        Toast.makeText(this, UserApi.getInstance().getEmail(), Toast.LENGTH_SHORT).show();

        //bydefault home
        Home home=new Home();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameL,new Home()).addToBackStack(null).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.frameL,new AddFragment()).addToBackStack(null).commit();

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
                        UserApi userApi = UserApi.getInstance();
                        if(TextUtils.isEmpty(userApi.getBio()))
                        {
                            //go to create profile fragment
                            selected=new CreateProfileFragment();
                        }
                        else
                        {
                            selected=new ProfileFragment();
                        }

                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameL,selected).addToBackStack(null).commit();
                return true;
            }
        });

    }
//    private void checkUserStatus() {
//        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
//        if(currentUser == null ){
//            startActivity(new Intent(MainActivity.this,login_signup_getstarted.class));
//            finish();
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }

//    @Override
//    //inflate menu
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main,menu);
//
//        MenuItem menuItem = menu.findItem(R.id.search_btn);
//        menuItem.setVisible(false);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.logoutbtn:
//                firebaseAuth.signOut();
//                startActivity(new Intent(MainActivity.this,login_signup_getstarted.class));
//                finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

}