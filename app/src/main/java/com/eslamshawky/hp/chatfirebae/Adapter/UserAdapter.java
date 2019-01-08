package com.eslamshawky.hp.chatfirebae.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eslamshawky.hp.chatfirebae.Activities.MessageActivity;
import com.eslamshawky.hp.chatfirebae.Model.Chat;
import com.eslamshawky.hp.chatfirebae.Model.Chatlist;
import com.eslamshawky.hp.chatfirebae.Model.User;
import com.eslamshawky.hp.chatfirebae.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private   Context mContext;
    private List<User> mUsers;
    private boolean isChat ;
    private String theLastMessage;
    private static final String TAG = "mutee";

    public UserAdapter(Context mContext, List<User> mUsers,boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);
        return new  UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
        }

         if(isChat){
            lastMessage(user.getId(),viewHolder.last_msg);
        }
        else {
            viewHolder.last_msg.setVisibility(View.GONE);
        }


        if (isChat){
            if(user.getStatus().equals("online")){
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            }
            else {
                viewHolder.img_on.setVisibility(View.GONE);
                viewHolder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        ImageView profile_image;
        private ImageView img_on,img_off;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_item);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    // check the last message

    private void lastMessage(final String userid , final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUse = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                       if(chat.getReceiver().equals(firebaseUse.getUid())&& chat.getSender().equals(userid)||
                           chat.getReceiver().equals(userid)&& chat.getSender().equals(firebaseUse.getUid())){
                           System.out.println(chat.getMessage());
                           Log.d(TAG,chat.getMessage());
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No message");
                        break;

                        default:
                            last_msg.setText(theLastMessage);
                            break;
                }

                theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
