package com.example.footer.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footer.MainActivity;
import com.example.footer.R;
import com.example.footer.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ambil username dari SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        
        // Set username ke TextView
        TextView usernameText = binding.textUsername;
        usernameText.setText(username);

        // Tambahkan ini untuk menangani klik button Account Settings
        binding.btnAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileSetting.class);
                startActivity(intent);
            }
        });

        // Tambahkan ini untuk menampilkan avatar default
        binding.imageEllipse.setImageResource(R.drawable.icon_reading);
        
        // Tambahkan handler untuk button logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hapus data user dari SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                // Kembali ke MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
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