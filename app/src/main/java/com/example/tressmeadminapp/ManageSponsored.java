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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tressmeadminapp.Adapters.SponsoredItemListAdaper;
import com.example.tressmeadminapp.Model.Hairstyles;
import com.example.tressmeadminapp.Model.Sponsored;
import com.example.tressmeadminapp.databinding.ActivityManageSponsoredBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageSponsored extends AppCompatActivity {

    private ActivityManageSponsoredBinding b;
    private List<Sponsored> sponsoredList;
    private DatabaseReference db;
    private SponsoredItemListAdaper adaper;
    private String image = "";
    private Uri uri;
    private ProgressDialog dialog;
    private ImageView imageView;
    private Bitmap bitmap = null;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityManageSponsoredBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        sponsoredList = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(ManageSponsored.this);
        db = FirebaseDatabase.getInstance().getReference().child("Sponsored");
     /*   String key = db.push().getKey();
        Sponsored model = new Sponsored(key,"","");
        db.child(key).setValue(model);*/
        b.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageSponsored.this, MainScreen.class));
                finish();
            }
        });
        getSponsoredList();
    }

    private void getSponsoredList() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sponsoredList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Sponsored model = ds.getValue(Sponsored.class);
                        sponsoredList.add(model);
                    }
                    adaper = new SponsoredItemListAdaper(ManageSponsored.this, sponsoredList);
                    b.gridview.setAdapter(adaper);
                    adaper.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view) {
                            showSponsoredDialogBox(position);
                        }
                    });
                    adaper.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showSponsoredDialogBox(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageSponsored.this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_sponsor_custom_layout, null);
        TextView titleTxt = add_view.findViewById(R.id.title);
        EditText nameTxt = add_view.findViewById(R.id.nameTxt);
        imageView = add_view.findViewById(R.id.image);
        Button addBtn = add_view.findViewById(R.id.add);
        Button cancelBtn = add_view.findViewById(R.id.cancel);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameTxt.getText().toString();
                String key = db.push().getKey();
                if (!name.isEmpty() && !image.equals("")) {
                    Sponsored model = new Sponsored(key, name, image);
                    db.child(key).setValue(model);
                    getSponsoredList();
                    alertDialog.dismiss();
                }
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

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), 3);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            if (requestCode == 3) {
                uri = data.getData();
                imageView.setImageURI(uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    saveImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImage() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading....");
        dialog.show();
        if (uri != null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Sponsored")
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
            Toast.makeText(ManageSponsored.this, "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }
}