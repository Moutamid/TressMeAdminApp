package com.example.tressmeadminapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.tressmeadminapp.Model.Faqs;
import com.example.tressmeadminapp.R;

import java.util.List;

public class FaqsListAdapter extends RecyclerView.Adapter<FaqsListAdapter.FaqsViewHolder>{

    private Context mContext;
    private List<Faqs> faqsList ;

    public FaqsListAdapter(Context mContext, List<Faqs> faqsList) {
        this.mContext = mContext;
        this.faqsList = faqsList;
    }

    @NonNull
    @Override
    public FaqsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.faq_custom_layout,parent,false);
        return new FaqsViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqsViewHolder holder, int position) {
        Faqs model = faqsList.get(position);
        holder.titleTxt.setText(model.getTitle());
        holder.descriptionTxt.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return faqsList.size();
    }

    public class FaqsViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTxt,descriptionTxt;

        public FaqsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.title);
            descriptionTxt = itemView.findViewById(R.id.description);
        }
    }
}
