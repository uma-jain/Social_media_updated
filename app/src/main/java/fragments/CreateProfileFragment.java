package fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Utils.UserApi;

import static android.app.Activity.RESULT_OK;

public class CreateProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    //views
    ImageView addcover, addprofile, cover_container, profile_container;
    TextView nameEt, bioEt, ProffEt;
    Button createProfile;

    Uri profileUri = null;
    Uri coverUri = null;
    private static final int PICK_IMAGE = 1;
    String profileOrCover;
    UploadTask uploadTask;
    //realtime databse
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");

    public CreateProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_create_profile, container, false);
        //current user
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //storage
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_cover");
        //tv and iv
        addcover = (ImageView) view.findViewById(R.id.cp_addcover);
        addprofile = (ImageView) view.findViewById(R.id.cp_addprofile);
        cover_container = view.findViewById(R.id.cp_cover);
        profile_container = view.findViewById(R.id.cp_profileImage);
        nameEt = view.findViewById(R.id.cp_username);
        bioEt = view.findViewById(R.id.cp_bio);
        ProffEt = view.findViewById(R.id.cp_profession);
        createProfile = view.findViewById(R.id.cp_createProfile);


        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Create Profile");

        //add cover photo
        addcover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCover = "cover";
                Toast.makeText(getActivity(), "add cover", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        //add profile photo
        addprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrCover = "profile";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
                Toast.makeText(getActivity(), "add cover", Toast.LENGTH_LONG).show();
            }
        });
        //add data
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        return view;
    }

    private void uploadData() {
        final String bio = bioEt.getText().toString();
        final String username = nameEt.getText().toString();
        final String prof = ProffEt.getText().toString();
        //add data is profile
        final HashMap<String, Object> profile = new HashMap<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Creating Profile...It will take a while....");

        //check if all fields are filled
        if (!TextUtils.isEmpty(bio) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(prof) || String.valueOf(profileUri) != null || String.valueOf(coverUri) != null) {
            progressDialog.show();
            //upload both images to storage
            final StorageReference reference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExt(profileUri));
            final StorageReference reference1 = mStorageRef.child(System.currentTimeMillis() + "." + getFileExt(coverUri));
            //profile image
            uploadTask = reference.putFile(profileUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    Log.i("info", "inside upload profile");
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Log.i("info", "inside upload profile");
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri profiledownloadUri = task.getResult();
                    Log.i("info", "add upload profile in hashmap");
                    profile.put("image", profiledownloadUri.toString());
                }
            });
            //add cover image
            uploadTask = reference1.putFile(coverUri);
            Task<Uri> uriTask1 = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    Log.i("info", "inside upload cover");
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Log.i("info", "inside upload cover");
                    return reference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                //save to firestore
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    //realtime database
                    //add values in hashmap
                    Log.i("info", "inside upload data");
                    Uri coveruri = task.getResult();
                    profile.put("cover", coveruri.toString());
                    profile.put("username", username);
                    profile.put("bio", bio);
                    profile.put("profesion", prof);
                    //get user
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    databaseReference.child(currentUser.getUid()).updateChildren(profile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "PROFILE Created Successfully", Toast.LENGTH_LONG).show();
                                    //redirect to profile fragment
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                    //firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference documentReference;
                    //firestore storage
                    documentReference =db.collection("Users").document(currentUser.getUid());
                    documentReference.set(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // progressDialog.dismiss();
                            //   Toast.makeText(getActivity(), "PROFILE Created Successfully", Toast.LENGTH_LONG).show();
                            // redirect to profile fragment
                            //   Intent intent = new Intent(getActivity(), MainActivity.class);
                            //    startActivity(intent);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
                //local storage


            });


        } else {

            Toast.makeText(getActivity(), "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){

                if( profileOrCover=="profile"){
                    profileUri=data.getData();
                    Picasso.get().load(profileUri).into(profile_container);
                }else {
                    coverUri=data.getData();
                    Picasso.get().load(coverUri).into(cover_container);
                }

            }
        }catch (Exception e){
            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
        }
    }
    //for image storage
    private String getFileExt(Uri uri){

        ContentResolver contentResolver=getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
