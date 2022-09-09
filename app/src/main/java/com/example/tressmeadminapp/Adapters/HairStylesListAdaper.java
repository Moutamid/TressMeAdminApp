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

import com.example.tressmeadminapp.HairstyleDetails;
import com.example.tressmeadminapp.Model.Hairstyles;
import com.example.tressmeadminapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HairStylesListAdaper extends RecyclerView.Adapter<HairStylesListAdaper.HairStylesViewHolder> {

    private Context mContext;
    private List<Hairstyles> hairstylesList;

    public HairStylesListAdaper(Context mContext, List<Hairstyles> hairstylesList) {
        this.mContext = mContext;
        this.hairstylesList = hairstylesList;
    }

    @NonNull
    @Override
    public HairStylesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout,parent,false);
        return new HairStylesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HairStylesViewHolder holder, int position) {
        Hairstyles model = hairstylesList.get(position);
        holder.name.setText(model.getName());
        Picasso.with(mContext)
                .load(model.getImageUrl())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HairstyleDetails.class);
                intent.putExtra("model",model);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hairstylesList.size();
    }

    public class HairStylesViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name;

        public HairStylesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);
            name = itemView.findViewById(R.id.name);
        }
    }
}
