package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import Utils.UserApi;

public class Signup extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    ProgressDialog progressDialog;
    EditText userNameEt,emailEt,passwordEt;
    TextView redirect;
    Button registerbtn;
    SignInButton registerwithgooglebtn;
    private FirebaseAuth mAuth;


    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //collection reference , this collectionReference is going to point to Users collection
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userNameEt=findViewById(R.id.register_username);
        emailEt=findViewById(R.id.register_email);
        passwordEt=findViewById(R.id.register_password);
        redirect=findViewById(R.id.register_signuptologinredirect);
        registerbtn=findViewById(R.id.register_signInBtn);
        registerwithgooglebtn=findViewById(R.id.registerwithgooglebtn);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering user....");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
        registerwithgooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password,username;
                email=emailEt.getText().toString().trim();
                password=passwordEt.getText().toString().trim();
                username=userNameEt.getText().toString().trim();
                //validate email

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error msg
                    Log.i("invalid email","invlaid email");
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                }
                else if(password.length() < 4){
                    Log.i("invalid pass","invlaid pass");
                    passwordEt.setError("Password length cannot be less than 4 ");
                    passwordEt.setFocusable(true);
                }
                else if(TextUtils.isEmpty(username)){
                    Log.i("invalid username","invlaid pass");
                    userNameEt.setError("Username Cannot be Empty");
                    userNameEt.setFocusable(true);
                }
                else { registerUser(username,email,password);}
            }
        });
        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this,Login.class));
                finish();
            }
        });
    }

    private void registerUser(final String username, String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("info", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //store in realtime database
                            String email=user.getEmail();
                            String uid=user.getUid();
                            HashMap<Object,String> hashMap=new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("phone","");
                            hashMap.put("image","");
                            hashMap.put("bio","");
                            hashMap.put("username",username);
                            hashMap.put("profession","");
                            hashMap.put("cover","");
                            hashMap.put("follower", "0");
                            myRef.child(uid).setValue(hashMap);

                            //this will put the user data into firestore
                            collectionReference.add(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (Objects.requireNonNull(task.getResult()).exists())
                                                            {
                                                                String uid = task.getResult()
                                                                        .getString("uid");
                                                                String email = task.getResult()
                                                                        .getString("email");

                                                                //add into local api
                                                                UserApi userApi = UserApi.getInstance();
                                                                userApi.setUid(uid);
                                                                userApi.setEmail(email);
                                                                userApi.setBio("");
                                                                userApi.setCover("");
                                                                userApi.setFollowerCount("0");
                                                                userApi.setImage("");
                                                                userApi.setPhone("");
                                                                userApi.setProfession("");
                                                                userApi.setUsername(username);

                                                                //now pass to mainactivity
                                                                Toast.makeText(Signup.this, "Registered Successfully",  Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Signup.this, MainActivity.class));
                                                                finish();

                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w("info signup failure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("google auth", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("google failure", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success final", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //store in realtime database
                            String email=user.getEmail();
                            String uid=user.getUid();
                            HashMap<Object,String> hashMap=new HashMap<>();
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("phone","");
                            hashMap.put("image","");
                            hashMap.put("bio","");
                            hashMap.put("username",email);
                            hashMap.put("profession","");
                            hashMap.put("cover","");
                            hashMap.put("followerCount", "");

                            //this will put the user data into firebase
                            myRef.child(uid).setValue(hashMap);
                            //this will put the user data into firestore
                            collectionReference.whereEqualTo(uid,154854);
                            collectionReference.add(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (Objects.requireNonNull(task.getResult()).exists())
                                                            {
                                                                String uid = task.getResult()
                                                                        .getString("uid");
                                                                String email = task.getResult()
                                                                        .getString("email");

                                                                //add into local api
                                                                UserApi userApi = UserApi.getInstance();
                                                                userApi.setUid(uid);
                                                                userApi.setEmail(email);
                                                                userApi.setBio("");
                                                                userApi.setCover("");
                                                                userApi.setFollowerCount("0");
                                                                userApi.setImage("");
                                                                userApi.setPhone("");
                                                                userApi.setProfession(email);
                                                                userApi.setUsername("");

                                                                //now pass to mainactivity
                                                                Toast.makeText(Signup.this, "Welcome ",
                                                                        Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Signup.this, MainActivity.class));
                                                                finish();

                                                            }
                                                        }
                                                    });
                                        }
                                    });


//                            startActivity(new Intent(Signup.this,MainActivity.class));
//                            finish();
                            //
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google signin failed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Signup.this," Login through Google Failed",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Signup.this," Login through Google Failed",Toast.LENGTH_LONG).show();

                    }
                });
    }
}
