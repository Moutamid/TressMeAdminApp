package com.example.tressmeadminapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tressmeadminapp.Model.Chat;
import com.example.tressmeadminapp.Model.User;
import com.example.tressmeadminapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatSupportRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_TIME = 0;
    private final int TYPE_INCOMING = 1;
    private final int TYPE_OUTGOING = 2;
    List<Chat> chatList;
    private Context mContext;
    String fname = "";


    public ChatSupportRoomAdapter(Context context, List<Chat> chats) {
        this.mContext = context;
        this.chatList = chats;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);

        return tes(chat);
      }

    private int tes(Chat chat) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(chat.getSenderUid())) {
            return TYPE_OUTGOING;
        }
        return TYPE_INCOMING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == TYPE_INCOMING){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_incoming, parent, false);
            return new IncomingViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_outgoing, parent, false);
            return new OutgoingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_INCOMING) {
            IncomingViewHolder holder1 = (IncomingViewHolder) holder;
            configureViewHolderIncoming(holder1, position);
        }  else{
            OutgoingViewHolder holder2 = (OutgoingViewHolder) holder;
            configureViewholderOutgoing(holder2, position);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    private void configureViewHolderIncoming(final IncomingViewHolder holder, int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Chat chat = (Chat) chatList.get(position);
        if (chat != null) {

            getProfilePic(chat.getSenderUid(),holder.imageView);
            holder.linearLayout.setBackgroundResource(R.drawable.chat_outgoing_background);
                if (chat.getType().equals("text")) {
                    holder.message.setVisibility(View.VISIBLE);
                    holder.iuploadImg.setVisibility(View.GONE);
                    holder.message.setText(chat.getMessage());

                    holder.message.setTextColor(Color.WHITE);
                } else if (chat.getType().equals("image")) {

                    holder.message.setVisibility(View.GONE);
                    holder.iuploadImg.setVisibility(View.VISIBLE);
                    Picasso.with(mContext)
                            .load(chat.getMessage())
                            .into(holder.iuploadImg);
                }
        }

    }

    private void getProfilePic(String receiverUid, CircleImageView imageView) {

      //  Toast.makeText(mContext,receiverUid,Toast.LENGTH_LONG).show();

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(receiverUid);
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()){
                    User model = ds.getValue(User.class);
                    if (model.getImageUrl().equals("")){
                        Picasso.with(mContext)
                                .load(R.drawable.logo)
                                .into(imageView);
                    }else{
                        Picasso.with(mContext)
                                .load(model.getImageUrl())
                                .into(imageView);
                    }
                    fname = model.getName();

                }else {
                    Picasso.with(mContext)
                            .load(R.drawable.logo)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void configureViewholderOutgoing(final OutgoingViewHolder holder, int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();
        Chat chat = (Chat) chatList.get(position);
        if (chat != null) {
            //if (chat.getReplyId() == 0) {

               // holder.reply_layout.setVisibility(View.GONE);
                if (chat.getType().equals("text")) {

                    holder.message.setVisibility(View.VISIBLE);
                    holder.ouploadImg.setVisibility(View.GONE);
                    holder.message.setText(chat.getMessage());
                    //checkOutgoingReply(chat,holder);
                } else if (chat.getType().equals("image")) {

                    holder.message.setVisibility(View.GONE);
                    holder.ouploadImg.setVisibility(View.VISIBLE);
                    Picasso.with(mContext)
                            .load(chat.getMessage())
                            .into(holder.ouploadImg);
                }
            holder.linearLayout.setBackgroundResource(R.drawable.chat_incoming_background);
        }
    }


    public class IncomingViewHolder extends RecyclerView.ViewHolder {
        private TextView message,reply_message,username;
        private ConstraintLayout constraintLayout;
        private RelativeLayout layout;
        private TextView time;
        private LinearLayout linearLayout;
        private CircleImageView imageView;
        private ImageView iuploadImg,reply_img;

        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profile);
            message = itemView.findViewById(R.id.tv_chat_incoming);
            constraintLayout = itemView.findViewById(R.id.layout_first_incoming);
            layout = itemView.findViewById(R.id.layout_chat_incoming);
            linearLayout = itemView.findViewById(R.id.linear);
            iuploadImg = itemView.findViewById(R.id.tv_img_incoming);
            username = itemView.findViewById(R.id.username);
        }

    }


    public class OutgoingViewHolder extends RecyclerView.ViewHolder {
        private TextView message,reply_message,username;
        private ConstraintLayout constraintLayout;
        private RelativeLayout layout;
        private TextView time;
        private ImageView ouploadImg;
        private LinearLayout linearLayout;

        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_chat_outgoing);
            username = itemView.findViewById(R.id.username);
            constraintLayout = itemView.findViewById(R.id.layout_first);
            layout = itemView.findViewById(R.id.layout_chat);
            linearLayout = itemView.findViewById(R.id.linear);
            //time = itemView.findViewById(R.id.tv_time_chat_outgoing);
            ouploadImg = itemView.findViewById(R.id.tv_img_outgoing);
        }
    }


    public void removeAt(int position){
        chatList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,chatList.size());
    }

    public void clearChat(){
        int size = chatList.size();
        chatList.clear();
        notifyItemRangeRemoved(0,size);
    }

}
