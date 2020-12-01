package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MessagesFragment;
import com.example.myapplication.Personal_Chat_Activity;
import com.example.myapplication.R;
import com.example.myapplication.SearchFragment;
import com.example.myapplication.login_signup_getstarted;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Utils.MessageModel;
import Utils.UserApi;
import Utils.UserModal;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter  extends RecyclerView.Adapter<MessageListAdapter.MyHolder>  {
    Context context;
    ArrayList<String> messagesList;
    UserApi userApi=UserApi.getInstance();

    //firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    CollectionReference collectionReference7;

    public MessageListAdapter(Context context, ArrayList<String> messageModalList, MessagesFragment messagesFragment) {
        this.context = (Context) context;
        this.messagesList = messageModalList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_message_user, parent, false);
        return new MessageListAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String senderid= messagesList.get(position);
        final String[] doc_Id = new String[1];
        //get this users details
        collectionReference.whereEqualTo("uid", senderid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {

                        if (error != null)
                        {
                            return;
                        }
                        if (!value.isEmpty())
                        {
                            for (QueryDocumentSnapshot snapshot:value)
                            {
                                holder.tv_username.setText(snapshot.getString("username"));
                                try {
                                    Picasso.get().load(snapshot.getString("image")).into(holder.imageView);
                                } catch (Exception e) {
                                    Picasso.get().load(R.drawable.ic_baseline_person_outline_24).into(holder.imageView);
                                }
                            }
                        }

                    }
                });

        //get the last message we have to generate unique id
        String u1 = senderid;
        String u2 = userApi.getUid();

        int res= stringCompare(u1, u2);
        final String concatUid;
        if(res<0)
        {
            concatUid = u1+"_"+u2;
        }
        else
        {
            concatUid = u2+"_"+u1;
        }
        Log.i("info",concatUid);
        collectionReference7 = db.collection("messages");

        //get access of messages collection and display last message
        collectionReference7.whereEqualTo("specialUid",concatUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Log.i("info", "start firestore procedure");
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        doc_Id[0] =(String) documentSnapshot.getId();
                        final CollectionReference documentReference1 = db.collection("messages").document(doc_Id[0]).collection("messagesInfo");
                        documentReference1.orderBy("messageTime", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                for (QueryDocumentSnapshot doc : value) {
                                    Log.i("info","insisde event3");
                                    MessageModel msg = doc.toObject(MessageModel.class);
                                    holder.tv_lastMessage.setText(msg.getMessageText());
                                    holder.last_time.setText(msg.getMessageTime());
                                }
                            }

                        });

                    }
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                // code to get the users info and put in usermodal;
                //we already have doc id
                //we have user id so get access of this users data and add in userModal

                Toast.makeText(context,doc_Id[0],Toast.LENGTH_LONG).show();
                CollectionReference collectionReference8 = db.collection("Users");
                collectionReference8.whereEqualTo("uid",senderid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("info", "start firestore procedure");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                UserModal user  = documentSnapshot.toObject(UserModal.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("userModel", user);
                                bundle.putString("documentId", doc_Id[0]);
                                Log.d("here6", "onUserClick:");
                                Intent intent = new Intent(context, Personal_Chat_Activity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        }
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder  {

        CircleImageView imageView;
        TextView tv_username, tv_lastMessage,last_time;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_username=itemView.findViewById(R.id.row_message_username);
            tv_lastMessage=itemView.findViewById(R.id.row_message_user_lastmsg);
            last_time=itemView.findViewById(R.id.row_message_user_lastmessagedtime);
            imageView=itemView.findViewById(R.id.row_message_user_civ_profilepic);
        }


    }
    public int stringCompare(String str1, String str2)
    {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        if (l1 != l2) {
            return l1 - l2;
        }

        else {
            return 0;
        }
    }
}
