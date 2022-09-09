package com.example.tressmeadminapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tressmeadminapp.Messages;
import com.example.tressmeadminapp.Model.Conversation;
import com.example.tressmeadminapp.Model.User;
import com.example.tressmeadminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private List<Conversation> conversationList;
    private DatabaseReference mUserReference;


    private Context mContext;

    public ChatListAdapter(Context context, List<Conversation> conversations) {
        this.mContext = context;
        this.conversationList = conversations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userUid = mUser.getUid();
        Conversation conversation = conversationList.get(position);
  //      if (!conversation.isHide()) {
            String id = conversation.getChatWithId();
            Log.i("listadapter", "id: " + id);

        Query query = mUserReference.orderByChild("uId").equalTo(id);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                   for (DataSnapshot ds : dataSnapshot.getChildren()){
                       final User user = ds.getValue(User.class);
                       holder.username.setText(user.getName());
                       if (user.getImageUrl().equals("")){
                           Picasso.with(mContext)
                                   .load(R.drawable.logo)
                                   .into(holder.avatar);
                       }else{
                           Picasso.with(mContext)
                                   .load(user.getImageUrl())
                                   .into(holder.avatar);
                       }

                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Conversation conversation = conversationList.get(position);
                             //  clearUnreadChat(conversation.getChatWithId());
                               Intent intent = new Intent(mContext, Messages.class);
                              // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               intent.putExtra(Messages.EXTRAS_USER, user);
                               intent.putExtra("userUid", conversation.getChatWithId());
                               mContext.startActivity(intent);

                           }
                       });

                   }}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            calculateTimeAgo(conversation.getTimestamp(),holder.chatTime);

        if (!conversation.getLastMessage().equals("")) {
                if (conversation.getType().equals("text")) {
                    holder.message.setText(conversation.getLastMessage());
                } else if (conversation.getType().equals("image")) {
                    holder.message.setText("Photo");
                    holder.message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera, 0, 0, 0);
                    // holder.message.setGravity(Gravity.CENTER);
                }


                //unreadMsg(conversation.getChatWithId(), holder.unreadCount);
               // getMessageStatus(conversation, holder.deliveredMsg, holder.seenMsg, position);
            }else {
                holder.message.setText("");
            }



    }

    private void calculateTimeAgo(long timestamp, TextView chatTime) {
        Date timeD = new Date(timestamp * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(timeD);
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            chatTime.setText(ago);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

  /*  private void getMessageStatus(Conversation conversation, ImageView deliveredMsg, ImageView seenMsg,int position) {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String userUid = mUser.getUid();
        String otherId = conversation.getChatWithId();

        chatReference.child(userUid).child(otherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Chat mUserChat = ds.getValue(Chat.class);
                    if (mUserChat.getSenderUid().equals(userUid)){
                      //  if (position == conversationList.size() - 1) {
                            if (mUserChat.isMsgStatus()) {
                                seenMsg.setVisibility(View.VISIBLE);
                                deliveredMsg.setVisibility(View.GONE);
                            } else {
                                deliveredMsg.setVisibility(View.VISIBLE);
                                seenMsg.setVisibility(View.GONE);
                            }
                       // }
                    }else{

                        deliveredMsg.setVisibility(View.GONE);
                        seenMsg.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private TextView message;
        //private CircleImageView avatar,online,offline;
        private RelativeLayout layout;
        private TextView unreadCount;
        private TextView chatTime;
        private ImageView seenMsg,deliveredMsg,avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            avatar = itemView.findViewById(R.id.profile);
            //online = itemView.findViewById(R.id.img_on);
            //offline = itemView.findViewById(R.id.img_off);
            layout = itemView.findViewById(R.id.layout_user_chat);
            //unreadCount = itemView.findViewById(R.id.arrival);
            chatTime = itemView.findViewById(R.id.msgTime);
            //seenMsg = itemView.findViewById(R.id.seen);
            //deliveredMsg = itemView.findViewById(R.id.delivered);
        }
    }

  /*  private void clearUnreadChat(String chatWithId) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference().child("conversation").child(mFirebaseUser.getUid()).child(chatWithId).child("unreadChatCount");
        conversationReference.setValue(0);


    }

    private void unreadMsg(final String userId, final TextView msgCount){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("chats");
        String userUid = firebaseUser.getUid();

        db.child(userUid).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat message = ds.getValue(Chat.class);
                    if (message.getReceiverUid().equals(firebaseUser.getUid()) && message.getSenderUid().equals(userId) &&
                            !message.isMsgStatus()){
                        unread++;
                    }

                }
                if (unread == 0) {
                    msgCount.setVisibility(View.GONE);
                }else {
                    msgCount.setVisibility(View.VISIBLE);
                    msgCount.setText(String.valueOf(unread));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

}
