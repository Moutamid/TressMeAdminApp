package com.example.tressmeadminapp.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tressmeadminapp.Adapters.HairStylesListAdaper;
import com.example.tressmeadminapp.Adapters.ProductTestingListAdaper;
import com.example.tressmeadminapp.Adapters.SkincareTipListAdaper;
import com.example.tressmeadminapp.Model.Hairstyles;
import com.example.tressmeadminapp.Model.ProductModel;
import com.example.tressmeadminapp.Model.User;
import com.example.tressmeadminapp.R;
import com.example.tressmeadminapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView hairStylesList,tipsList,productList;
    private TextView username,addTips,addProduct,addStyle;
    private FirebaseAuth mAuth;
    private DatabaseReference skinDB,productDB,db;
    List<Hairstyles> hairstyles;
    private TextView titleTxt;
    private EditText nameTxt,urlTxt;
    private Button addBtn,cancelBtn;
    private ImageView addImage,hairstyleImg;
    private Bitmap bitmap;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    StorageReference mStorage;
    ProgressDialog dialog;
    private String images = "";
    private String hairstyle = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        hairStylesList = root.findViewById(R.id.hair_styles_list);
        tipsList = root.findViewById(R.id.skin_tips_list);
        productList = root.findViewById(R.id.product_list);
        username = root.findViewById(R.id.name);
        addTips = root.findViewById(R.id.add_tips);
        addStyle = root.findViewById(R.id.add_style);
        addProduct = root.findViewById(R.id.add_products);
        skinDB = FirebaseDatabase.getInstance().getReference().child("SkinTips");
        productDB = FirebaseDatabase.getInstance().getReference().child("Products");
        db = FirebaseDatabase.getInstance().getReference().child("Hairstyles");
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        username.setText("Admin");
        dialog = new ProgressDialog(getActivity());
        checkPermission();
        getHairStyles();
        getSkincareTips();
        getProducts();

        addTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTipsDialogBox();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsDialogBox();

            }
        });
        addStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHairstyleDialogBox();
            }
        });
        return root;
    }

    private void showHairstyleDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_style_custom_layout,null);
        titleTxt = add_view.findViewById(R.id.title);
        nameTxt = add_view.findViewById(R.id.nameTxt);
        hairstyleImg = add_view.findViewById(R.id.image);
        addBtn = add_view.findViewById(R.id.add);
        cancelBtn = add_view.findViewById(R.id.cancel);
        hairstyleImg.setOnClickListener(new View.OnClickListener() {
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
                String name = nameTxt.getText().toString();
                String key = db.push().getKey();
                if (!name.isEmpty() && !hairstyle.equals("")){
                    Hairstyles model = new Hairstyles(key,name,hairstyle);
                    db.child(key).setValue(model);
                    getHairStyles();
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

    private void openHairstyleGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),3);
    }

    private void showProductsDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_item_custom_layout,null);
        titleTxt = add_view.findViewById(R.id.title);
        nameTxt = add_view.findViewById(R.id.nameTxt);
        urlTxt = add_view.findViewById(R.id.urlTxt);
        addImage = add_view.findViewById(R.id.image);
        addBtn = add_view.findViewById(R.id.add);
        cancelBtn = add_view.findViewById(R.id.cancel);
        titleTxt.setText("Add New Product Test");
        addImage.setOnClickListener(new View.OnClickListener() {
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
                String url = urlTxt.getText().toString();
                String key = productDB.push().getKey();
                if (!name.isEmpty() && !url.isEmpty() && !images.equals("")){
                    ProductModel model = new ProductModel(key,name,images,url);
                    productDB.child(key).setValue(model);
                    getProducts();
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

    private void showTipsDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_item_custom_layout,null);
        titleTxt = add_view.findViewById(R.id.title);
        nameTxt = add_view.findViewById(R.id.nameTxt);
        urlTxt = add_view.findViewById(R.id.urlTxt);
        addImage = add_view.findViewById(R.id.image);
        addBtn = add_view.findViewById(R.id.add);
        cancelBtn = add_view.findViewById(R.id.cancel);
        titleTxt.setText("Add New Skin Tips");
        addImage.setOnClickListener(new View.OnClickListener() {
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
                String url = urlTxt.getText().toString();
                String key = skinDB.push().getKey();
                if (!name.isEmpty() && !url.isEmpty() && !images.equals("")){
                    ProductModel model = new ProductModel(key,name,images,url);
                    skinDB.child(key).setValue(model);
                    getSkincareTips();
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

    public void checkPermission()
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_PERMISSION_CODE);
        }
        else {
            //Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK &&
                data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                uri = data.getData();
                addImage.setImageURI(uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    saveInformation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == 3){
                uri = data.getData();
                hairstyleImg.setImageURI(uri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
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
            hairstyleImg.setDrawingCacheEnabled(true);
            hairstyleImg.buildDrawingCache();
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
                                hairstyle = downloadUri.toString();
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
            Toast.makeText(getActivity(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }

    private void saveInformation() {

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading your product....");
        dialog.show();
        if (uri != null) {
            addImage.setDrawingCacheEnabled(true);
            addImage.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Products").child(System.currentTimeMillis() + ".jpg");
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
                                images = downloadUri.toString();
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
            Toast.makeText(getActivity(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }

    private void getHairStyles() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        hairStylesList.setLayoutManager(manager);
        hairstyles = new ArrayList<>();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    hairstyles.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Hairstyles model = ds.getValue(Hairstyles.class);
                        hairstyles.add(model);
                    }
                    HairStylesListAdaper adapter = new HairStylesListAdaper(getActivity(),hairstyles);
                    hairStylesList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSkincareTips() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        tipsList.setLayoutManager(manager);
        List<ProductModel> hairstyles = new ArrayList<>();

        skinDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    hairstyles.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        ProductModel model = ds.getValue(ProductModel.class);
                        hairstyles.add(model);
                    }
                    SkincareTipListAdaper adapter = new SkincareTipListAdaper(getActivity(),hairstyles);
                    tipsList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getProducts() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        productList.setLayoutManager(manager);
        List<ProductModel> hairstyles = new ArrayList<>();

        productDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    hairstyles.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        ProductModel model = ds.getValue(ProductModel.class);
                        hairstyles.add(model);
                    }
                    ProductTestingListAdaper adapter = new ProductTestingListAdaper(getActivity(),hairstyles);
                    productList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}