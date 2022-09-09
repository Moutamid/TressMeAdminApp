package com.example.tressmeadminapp.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tressmeadminapp.FaqActivity;
import com.example.tressmeadminapp.Model.Faqs;
import com.example.tressmeadminapp.R;
import com.example.tressmeadminapp.databinding.FragmentUserBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private TextView faqTxt,notificationtxt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        faqTxt = root.findViewById(R.id.faq);
        notificationtxt = root.findViewById(R.id.notification);
        faqTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FaqActivity.class));
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}