package com.example.tressmeadminapp.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tressmeadminapp.Adapters.ChatListAdapter;
import com.example.tressmeadminapp.Model.Conversation;
import com.example.tressmeadminapp.Model.User;
import com.example.tressmeadminapp.R;
import com.example.tressmeadminapp.databinding.FragmentMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding;
    private RecyclerView conversationList;
    private List<User> users = new ArrayList<>();
    private List<Conversation> conversations = new ArrayList<>();
    DatabaseReference mConversationReference;
    String myId;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ChatListAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        conversationList = root.findViewById(R.id.msg_list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        conversationList.setLayoutManager(manager);
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation")
                .child(user.getUid());
        myId = user.getUid();
        //getUserChatList();
        getGeneralChatList();
        return root;
    }
    private void getGeneralChatList() {
        Query myQuery = mConversationReference.orderByChild("timestamp").limitToFirst(10);
        myQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversations.clear();
                if (dataSnapshot.exists()) {
                    //  mStartChatLayout.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Conversation conversation = snapshot.getValue(Conversation.class);
                        // if (!conversation.isArchive()) {
                        conversations.add(conversation);
                        //}
                    }
                    mAdapter = new ChatListAdapter(getActivity(), conversations);
                    conversationList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}