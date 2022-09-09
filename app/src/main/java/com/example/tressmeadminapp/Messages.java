package com.example.tressmeadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tressmeadminapp.Adapters.ChatSupportRoomAdapter;
import com.example.tressmeadminapp.Model.Chat;
import com.example.tressmeadminapp.Model.Conversation;
import com.example.tressmeadminapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Messages extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView profileImg,sendImg,fileImg,backImg;
    private TextView usernameTxt;
    private EditText msgtxt;
    private String message="";
    public static final String EXTRAS_USER = "user";
    private DatabaseReference mChatReference,mConversationReference,mUserReference;
    private StorageReference mStorage;
    private ChatSupportRoomAdapter adapters;
    public String id,userUid;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    Bitmap bitmap;
    public static String idFromContact = null;
    FirebaseAuth mAuth;
    FirebaseUser user;
    User mUser;
    private String default_message = "";
    private List<Chat> chatList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = findViewById(R.id.recyclerView);
        profileImg = findViewById(R.id.profile);
        sendImg = findViewById(R.id.send);
        fileImg = findViewById(R.id.file);
        backImg = findViewById(R.id.back);
        usernameTxt = findViewById(R.id.username);
        msgtxt = findViewById(R.id.message);
        idFromContact = getIntent().getStringExtra("userUid");
        mUser = getIntent().getParcelableExtra(EXTRAS_USER);
        default_message = getIntent().getStringExtra("message");
        id = idFromContact;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        LinearLayoutManager manager = new LinearLayoutManager(Messages.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        mChatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();
        initializeToolbar();
        getChatData();
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contoh = msgtxt.getText().toString();
                if (!TextUtils.isEmpty(contoh)) {
                    long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    sentChat("text",contoh,timestamp);
                }
            }
        });
        fileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });


    }
    private void sentChat(String type, String contoh, long timestamp) {
        Chat chatReciever = new Chat(type, contoh, user.getUid(), idFromContact, timestamp);

        Conversation conversationSender = new Conversation(type, user.getUid(), idFromContact, contoh, timestamp);

        DatabaseReference senderReference = mConversationReference.child(user.getUid()).child(idFromContact);
        senderReference.setValue(conversationSender);

        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(idFromContact);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = mChatReference.child(idFromContact).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        msgtxt.setText("");
    }

    private void getChatData() {
        mChatReference.child(userUid).child(idFromContact).orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chatList.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Chat chat = ds.getValue(Chat.class);
                                chatList.add(chat);
                            }

                            adapters = new ChatSupportRoomAdapter(Messages.this, chatList);
                            recyclerView.smoothScrollToPosition(chatList.size() - 1);
                            recyclerView.setAdapter(adapters);
                            adapters.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void initializeToolbar() {
        usernameTxt.setText(mUser.getName());
        Picasso.with(Messages.this)
                .load(mUser.getImageUrl())
                .placeholder(R.drawable.logo)
                .into(profileImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Messages.this, MainScreen.class));
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                uploadImg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void uploadImg(){
        if (uri != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Messages").child("images")
                    .child(user.getUid()).child(System.currentTimeMillis() + ".jpg");
            final UploadTask uploadTask = reference.putBytes(thumb_byte_data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                sentChat("image",downloadUri.toString(),timestamp);
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            //    addChatList(senderId,rId);

        } else {
            Toast.makeText(Messages.this, "Please Choose a picture", Toast.LENGTH_LONG).show();
        }
    }

}