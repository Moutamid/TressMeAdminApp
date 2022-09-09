package com.example.tressmeadminapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tressmeadminapp.Model.Hairstyles;
import com.example.tressmeadminapp.Model.ProductModel;
import com.example.tressmeadminapp.ProductTestingDetails;
import com.example.tressmeadminapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SkincareTipListAdaper extends RecyclerView.Adapter<SkincareTipListAdaper.SkincareTipViewHolder>{

    private Context mContext;
    private List<ProductModel> hairstylesList;

    public SkincareTipListAdaper(Context mContext, List<ProductModel> hairstylesList) {
        this.mContext = mContext;
        this.hairstylesList = hairstylesList;
    }

    @NonNull
    @Override
    public SkincareTipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout,parent,false);
        return new SkincareTipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkincareTipViewHolder holder, int position) {
        ProductModel model = hairstylesList.get(position);
        holder.name.setText(model.getName());
        Picasso.with(mContext)
                .load(model.getImage())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductTestingDetails.class);
                intent.putExtra("url",model.getUrl());
                intent.putExtra("id",model.getId());
                intent.putExtra("type","skin");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hairstylesList.size();
    }

    public class SkincareTipViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;

        public SkincareTipViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);
            name = itemView.findViewById(R.id.name);
        }
    }
}
