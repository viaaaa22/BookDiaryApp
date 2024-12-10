package com.example.footer.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.widget.ImageButton;

import com.example.footer.MainActivity;
import com.example.footer.NavButton;
import com.example.footer.R;
import com.example.footer.DatabaseHandler;

public class ProfileSetting extends AppCompatActivity {
    private DatabaseHandler db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting);
        
        db = new DatabaseHandler(this);

        // Ambil username dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        int userId = db.getUserId(username);
        
        // Set email dan username ke TextView
        TextView emailText = findViewById(R.id.text_email);
        TextView usernameText = findViewById(R.id.text_username);
        
        String email = db.getEmailByUserId(userId);
        emailText.setText(email);
        usernameText.setText(username);

        // Set avatar default
        ImageView avatarImage = findViewById(R.id.image_ellipse);
        avatarImage.setImageResource(R.drawable.icon_reading);

        // Handle tombol Edit
        Button btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileSetting.this, EditProfile.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        // Handle tombol Delete Account
        Button btnDelete = findViewById(R.id.btn_delete_account);
        btnDelete.setOnClickListener(v -> {
            // Konfirmasi penghapusan akun
            new AlertDialog.Builder(this)
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Hapus akun
                    db.deleteUser(userId);
                    
                    // Hapus data SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    
                    // Kembali ke halaman MainActivity
                    Intent intent = new Intent(ProfileSetting.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Tidak", null)
                .show();
        });

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());
    }
}
