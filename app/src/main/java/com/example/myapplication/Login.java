package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

import Utils.UserApi;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    EditText emailEt,passwordEt;
    Button loginbtn;
    SignInButton logingooglebtn;
    TextView redirect,recover;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //Firestore conncetion - its gonna be db where will store our collction
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt=findViewById(R.id.login_email);
        passwordEt=findViewById(R.id.login_password);
        redirect=findViewById(R.id.login_logintoregisterredirect);
        loginbtn=findViewById(R.id.login_loginbtn);
        logingooglebtn=findViewById(R.id.login_withgoogle);
        recover=findViewById(R.id.login_recoverPassword);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Logging In....");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
        //-------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });
        logingooglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password;
                email=emailEt.getText().toString().trim();
                password=passwordEt.getText().toString().trim();
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
                else { login(email,password);}
            }
        });
        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Signup.class));

            }
        });

    }

    private void recoverPassword() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailEt=new EditText(this);
        emailEt.setHint("Enter Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setWidth(650);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(20,10,10,20);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String email=emailEt.getText().toString().trim();
                beginRecovery(email);
            }


        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending Email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Email sent",Toast.LENGTH_LONG).show();

                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Action Couldnt be Completed",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this,"Action Couldnt be Completed"+e.getMessage(),Toast.LENGTH_LONG).show();


                    }
                });

    }

    private void login(String email, String password) {
        progressDialog.setMessage("Logging In....");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login success", "loginInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            collectionReference
                                    .whereEqualTo("uid", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                            }
                                            assert value != null;
                                            if (!value.isEmpty()) {
                                                for (QueryDocumentSnapshot snapshot : value) {
                                                    UserApi userApi = UserApi.getInstance();
                                                    userApi.setUid(snapshot.getString("uid"));
                                                    userApi.setEmail(snapshot.getString("email"));
                                                    userApi.setBio(snapshot.getString("bio"));
                                                    userApi.setCover(snapshot.getString("cover"));
                                                    userApi.setImage(snapshot.getString("image"));
                                                    userApi.setPhone(snapshot.getString("phone"));
                                                    userApi.setProfession(snapshot.getString("profession"));
                                                    userApi.setUsername(snapshot.getString("username"));
                                                    userApi.setFollowerCount(snapshot.getString("followerCount"));
                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });


                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w("failure", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Invalid Credentials",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
                            Toast.makeText(Login.this, "User Logged In",
                                    Toast.LENGTH_SHORT).show();

                            currentUser = mAuth.getCurrentUser();
                            String currentUserId = currentUser.getUid();

                            collectionReference
                                    .whereEqualTo("uid", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {

                                            }
                                            if (!value.isEmpty()) {
                                                for (QueryDocumentSnapshot snapshot : value) {
                                                    UserApi userApi = UserApi.getInstance();
                                                    userApi.setUid(snapshot.getString("uid"));
                                                    userApi.setEmail(snapshot.getString("email"));
                                                    userApi.setBio(snapshot.getString("bio"));
                                                    userApi.setCover(snapshot.getString("cover"));
                                                    userApi.setImage(snapshot.getString("image"));
                                                    userApi.setPhone(snapshot.getString("phone"));
                                                    userApi.setProfession(snapshot.getString("profession"));
                                                    userApi.setUsername(snapshot.getString("username"));
                                                    userApi.setFollowerCount(snapshot.getString("followerCount"));

                                                    startActivity(new Intent(Login.this, MainActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    });



                            //startActivity(new Intent(Login.this,MainActivity.class));
                            //
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google signin failed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this," Login through Google Failed",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this," Login through Google Failed",Toast.LENGTH_LONG).show();

                    }
                });
    }

}