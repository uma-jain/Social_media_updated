package fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.myapplication.ProfileFragment;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Utils.UserApi;

import static android.app.Activity.RESULT_OK;

public class UpdateProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;

    //views
    ImageView addcover,addprofile,cover_container,profile_container;
    TextView nameEt,bioEt,ProffEt;
    Button updateProfile;

    Uri profileUri=null;
    Uri coverUri=null;
    private static final int PICK_IMAGE=1;
    String profileOrCover;
    Boolean coverUpdated=false;
    Boolean profileUpdated=false;
    UploadTask uploadTask;
    String updateImage=null;
    //realtime databse
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    FirebaseUser currentUser;

    final HashMap<String, Object> profile = new HashMap<>();


    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    public UpdateProfileFragment() {
        // Required empty public constructor
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);

        //initialize var
        firebaseAuth =FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference;
        //firestore storage
        documentReference = db.collection("Users").document(currentUser.getUid());
        //storage
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_cover");
        //tv and iv
        addcover=(ImageView) view.findViewById(R.id.up_addcover);
        addprofile=(ImageView) view.findViewById(R.id.up_addprofile);
        cover_container=view.findViewById(R.id.up_cover);
        profile_container=view.findViewById(R.id.up_profileImage);
        nameEt=view.findViewById(R.id.up_username);
        bioEt=view.findViewById(R.id.up_bio);
        ProffEt=view.findViewById(R.id.up_profession);
        updateProfile=view.findViewById(R.id.up_UpdateProfile);



       //set all the existing values
        final UserApi userApi=UserApi.getInstance();
        bioEt.setText(userApi.getBio());
        ProffEt.setText(userApi.getProfession());
        nameEt.setText(userApi.getUsername());

        try {
            Log.i("info", userApi.getImage());
            Picasso.get().load(userApi.getImage()).into(profile_container);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(profile_container);
        }
        try {
            Picasso.get().load(userApi.getCover()).into(cover_container);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(cover_container);
        }
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
        //update btn action
        updateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String bio = bioEt.getText().toString();
                final String username = nameEt.getText().toString();
                final String prof = ProffEt.getText().toString();
                //check whether data in text boxes and image uris are diff or not
                if (!TextUtils.isEmpty(bio) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(prof)) {
                    {  //nothing is empty
                        //where to call upload data();
                        if(profileUpdated&&coverUpdated){
                            updateImage="both";
                        }
                        if(!profileUpdated&&!coverUpdated){
                            updateImage="none";
                        }
                        if(profileUpdated&&!coverUpdated){
                            updateImage="profile";
                        }
                        if(!profileUpdated&&coverUpdated){
                            updateImage="cover";
                        }
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Updating Profile...It will take a while....");
                        progressDialog.show();
                        //craete a hashmap
                         //check which fields are chaneged and set hashmap
                        if (!username.equals(userApi.getUsername())) {
                            profile.put("username", username);
                            userApi.setUsername(username);
                            // Toast.makeText(getActivity(),profile.get("username").toString(),Toast.LENGTH_LONG).show();
                        }
                        if (!bio.equals(userApi.getBio())) {
                            profile.put("bio", bio);
                            userApi.setBio(bio);
                            //  Toast.makeText(getActivity(),profile.get("bio").toString(),Toast.LENGTH_LONG).show()
                        }
                        if (!prof.equals(userApi.getProfession())) {
                            profile.put("profession", prof);
                            userApi.setProfession(prof);
                            //  Toast.makeText(getActivity(),profile.get("profession").toString(),Toast.LENGTH_LONG).show();
                        }
                        //check if profile updated
                        if (profileUpdated) {
                            //add to storage
                            Log.i("info","profile updated");
                            final StorageReference reference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExt(profileUri));
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
                                    userApi.setImage((String) profile.get("image"));
                                    if(updateImage.equals("profile"))
                                       {
                                           Log.i("info", "profile");
                                           adddata();}
                                }
                            });

                        }
                        //check if cover updated
                        if (coverUpdated) {
                            Log.i("info", "cover updated");
                            //add to storage
                            final StorageReference reference1 = mStorageRef.child(System.currentTimeMillis() + "." + getFileExt(coverUri));
                            uploadTask = reference1.putFile(coverUri);
                            Task<Uri> uriTask1 = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    Log.i("info", "inside upload cover");
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return reference1.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                //save to firestore
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri coveruri = task.getResult();
                                    profile.put("cover", coveruri.toString());
                                    userApi.setCover((String) profile.get("cover"));
                                   if(updateImage.equals("both") || updateImage.equals("cover"))
                                   {
                                       Log.i("info", "both");

                                       adddata();}
                                }
                            });
                        }
                              //add to firestore
                        if( updateImage.equals("none"))
                           {
                               Log.i("info", "none");
                               adddata();
                           }
                    }
                }
            }

            private void adddata() {
                collectionReference.whereEqualTo("uid", currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("info", "start firestore procedure");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String doc_Id = documentSnapshot.getId();
                                final DocumentReference documentReference1 = db.collection("Users").document(doc_Id);
                                Log.i("info", "check firestore result");
                                documentReference1.update(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "profile Updated Successfully", Toast.LENGTH_LONG).show();
                                        //redirect to profile fragment

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
        return view;
    }
        public void onBackPressed() {
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameL, new ProfileFragment());
            fragmentTransaction.commit();
        }
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {

                if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data != null || data.getData() != null){

                    if( profileOrCover=="profile"){
                        profileUri=data.getData();
                        profileUpdated=true;
                        Picasso.get().load(profileUri).into(profile_container);
                    }else {
                        coverUri=data.getData();
                        coverUpdated=true;
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