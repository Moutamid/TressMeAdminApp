package com.example.tressmeadminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tressmeadminapp.Adapters.FaqsListAdapter;
import com.example.tressmeadminapp.Model.Faqs;
import com.example.tressmeadminapp.Model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    private ImageView backImg,addImg;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference db;
    private List<Faqs> faqsList;
    private EditText titleTxt,descriptionTxt;
    private Button addBtn,cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        backImg = (ImageView) findViewById(R.id.back);
        addImg = (ImageView) findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        db = FirebaseDatabase.getInstance().getReference().child("Faqs");
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FaqActivity.this,MainScreen.class));
            }
        });
        faqsList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(FaqActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        getFaqsList();
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFaqsDialogBox();
            }
        });
    }

    private void showFaqsDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaqActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View add_view = inflater.inflate(R.layout.add_new_faqs_custom_layout,null);
        titleTxt = add_view.findViewById(R.id.title);
        descriptionTxt = add_view.findViewById(R.id.description);
        addBtn = add_view.findViewById(R.id.add);
        cancelBtn = add_view.findViewById(R.id.cancel);

        builder.setView(add_view);
        AlertDialog alertDialog = builder.create();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleTxt.getText().toString();
                String description = descriptionTxt.getText().toString();
                if (!title.isEmpty() && !description.isEmpty()){
                    String key = db.push().getKey();
                    Faqs faqs = new Faqs(key,title,description);
                    db.child(key).setValue(faqs);
                    alertDialog.dismiss();
                    getFaqsList();
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


    private void getFaqsList() {

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    faqsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Faqs model = ds.getValue(Faqs.class);
                        faqsList.add(model);
                    }
                    FaqsListAdapter adapter = new FaqsListAdapter(FaqActivity.this,faqsList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}