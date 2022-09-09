package com.example.tressmeadminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tressmeadminapp.Model.Hairstyles;
import com.example.tressmeadminapp.databinding.ActivityHairstyleDetailsBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class HairstyleDetails extends AppCompatActivity {

    private ActivityHairstyleDetailsBinding b;
    private Hairstyles hairstyles;
    private DatabaseReference db;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    StorageReference mStorage;
    ProgressDialog dialog;
    private ImageView addImage;
    private String name,image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityHairstyleDetailsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        hairstyles= getIntent().getParcelableExtra("model");
        db = FirebaseDatabase.getInstance().getReference().child("Hairstyles");
        mStorage = FirebaseStorage.getInstance().getReference();
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HairstyleDetails.this,MainScreen.class));
                finish();
            }
        });
        name = hairstyles.getName();
        image = hairstyles.getImageUrl();
        b.title.setText(name);
        Picasso.with(HairstyleDetails.this)
                .load(image)
                .into(b.image);

        b.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.child(hairstyles.getId()).removeValue();
                startActivity(new Intent(HairstyleDetails.this,MainScreen.class));
                finish();
            }
        });
        b.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHairstyleDialogBox();
            }
        });
    }
    private void showHairstyleDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HairstyleDetails.this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_style_custom_layout,null);
        TextView nameTxt = add_view.findViewById(R.id.nameTxt);
        addImage = add_view.findViewById(R.id.image);

        nameTxt.setText(hairstyles.getName());
        Picasso.with(HairstyleDetails.this)
                .load(hairstyles.getImageUrl())
                .into(addImage);

        Button addBtn = add_view.findViewById(R.id.add);
        Button cancelBtn = add_view.findViewById(R.id.cancel);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHairstyleGallery();
            }
        });
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameTxt.getText().toString();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("name",name);
                hashMap.put("imageUrl",image);
                db.child(hairstyles.getId()).updateChildren(hashMap);
                alertDialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void openHairstyleGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                uri = data.getData();
                addImage.setImageURI(uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    saveHairstyle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveHairstyle() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading your hairstyle....");
        dialog.show();
        if (uri != null) {
            addImage.setDrawingCacheEnabled(true);
            addImage.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Hairstyles")
                    .child(System.currentTimeMillis() + ".jpg");
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
                                image = downloadUri.toString();
                                dialog.dismiss();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else {
            Toast.makeText(HairstyleDetails.this, "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }

}