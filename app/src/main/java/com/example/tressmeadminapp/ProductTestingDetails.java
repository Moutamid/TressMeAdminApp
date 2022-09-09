package com.example.tressmeadminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductTestingDetails extends AppCompatActivity {

    private ImageView backImg,deleteImg;
    private WebView webView;
    private String id = "";
    private String url = "";
    private String type = "";
    private DatabaseReference skinDB,productDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_testing_details);
        backImg = (ImageView) findViewById(R.id.back);
        deleteImg = (ImageView) findViewById(R.id.delete);
        webView = (WebView) findViewById(R.id.webview);
        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");
        skinDB = FirebaseDatabase.getInstance().getReference().child("SkinTips");
        productDB = FirebaseDatabase.getInstance().getReference().child("Products");
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductTestingDetails.this,MainScreen.class));
                finish();
            }
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("product")){
                    productDB.child(id).removeValue();
                }else {
                    skinDB.child(id).removeValue();
                }
                startActivity(new Intent(ProductTestingDetails.this,MainScreen.class));
                finish();
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 100);
            }
        });

        webView.loadUrl(url);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}