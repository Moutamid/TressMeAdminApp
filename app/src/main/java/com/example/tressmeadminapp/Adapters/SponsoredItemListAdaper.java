package com.example.tressmeadminapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.tressmeadminapp.ItemClickListener;
import com.example.tressmeadminapp.Model.Sponsored;
import com.example.tressmeadminapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SponsoredItemListAdaper extends BaseAdapter {
    Context context;
    List<Sponsored> hairstyles;
    LayoutInflater inflter;
    private ItemClickListener itemClickListener;

    public SponsoredItemListAdaper(Context applicationContext, List<Sponsored> hairstyles) {
        this.context = applicationContext;
        this.hairstyles = hairstyles;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return hairstyles.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.sponsored_custom_layout, null); // inflate the layout


        ImageView imageView = view.findViewById(R.id.image);
        ImageView deleteImg = view.findViewById(R.id.delete);
        ImageView addImg = view.findViewById(R.id.add);
        CardView cardView = view.findViewById(R.id.card);
        TextView name = view.findViewById(R.id.name);

        Sponsored model = hairstyles.get(i);

        if (model.getName().equals("") && model.getImageUrl().equals("")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cardView.setCardBackgroundColor(context.getColor(R.color.purple_700));
            }
            addImg.setVisibility(View.VISIBLE);
            deleteImg.setVisibility(View.GONE);
        }else {
            addImg.setVisibility(View.GONE);
            deleteImg.setVisibility(View.VISIBLE);
            name.setText(model.getName());
            Picasso.with(context)
                    .load(model.getImageUrl())
                    .into(imageView);
        }

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null){
                    itemClickListener.onItemClick(i,view);
                }
            }
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Sponsored");
                db.child(model.getId()).removeValue();
                hairstyles.remove(i);
                notifyDataSetChanged();
            }
        });

         return view;
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


}