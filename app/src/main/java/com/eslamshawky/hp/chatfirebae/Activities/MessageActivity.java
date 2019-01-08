package com.eslamshawky.hp.chatfirebae.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eslamshawky.hp.chatfirebae.Adapter.MessageAdapter;
import com.eslamshawky.hp.chatfirebae.Fragments.APIService;
import com.eslamshawky.hp.chatfirebae.Model.Chat;
import com.eslamshawky.hp.chatfirebae.Model.User;
import com.eslamshawky.hp.chatfirebae.Notifications.Client;
import com.eslamshawky.hp.chatfirebae.Notifications.Data;
import com.eslamshawky.hp.chatfirebae.Notifications.MyResponse;
import com.eslamshawky.hp.chatfirebae.Notifications.Sender;
import com.eslamshawky.hp.chatfirebae.Notifications.Token;
import com.eslamshawky.hp.chatfirebae.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView prfile_image;
    TextView username;
    EditText text_send;
    ImageButton  button_send;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;

    ValueEventListener seenListener;

    String userid;
    APIService apiService;
    boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("http://fm.googleapis.com/").create(APIService.class);


        recyclerView = findViewById(R.id.recycler_view_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        prfile_image = findViewById(R.id.profile_image_message);
        username = findViewById(R.id.username_message);

        text_send = findViewById(R.id.text_send);
        button_send = findViewById(R.id.button_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        button_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notify = true;
                String message = text_send.getText().toString();
                if(!message.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,message);
                }
                else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    prfile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(prfile_image);
                }
                readMessage(firebaseUser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(userid)){
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendMessage(String sender,final String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);
  // add user to chatFragment
        final String userid = intent.getStringExtra("userid");

        final DatabaseReference chatref = FirebaseDatabase.getInstance()
                .getReference("Chatlist").child(firebaseUser.getUid()).child(userid);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    chatref.child("id").setValue(userid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotifications(receiver, user.getUsername(), msg);
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifications(String receiver, final String username, final String message) {

        DatabaseReference refToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = refToken.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New message"
                    ,userid);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code()==200){
                                        if(response.body().success !=1){
                                            Toast.makeText(MessageActivity.this, "Failde", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             mChat.clear();

             for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                 Chat chat = snapshot.getValue(Chat.class);
                 if(chat.getReceiver().equals(myid)&& chat.getSender().equals(userid)||
                         chat.getReceiver().equals(userid)&& chat.getSender().equals(myid)){
                     mChat.add(chat);
                 }
                 messageAdapter  = new MessageAdapter(MessageActivity.this,mChat,imageurl);
                 recyclerView.setAdapter(messageAdapter);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("ofline");
    }
}
