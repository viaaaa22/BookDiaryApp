package com.example.footer.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.footer.R;
import com.example.footer.DatabaseHandler;

public class EditProfile extends AppCompatActivity {
    private DatabaseHandler db;
    private EditText emailInput, usernameInput, passwordInput, confirmPasswordInput;
    private String currentUsername;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        
        db = new DatabaseHandler(this);
        
        // Inisialisasi views
        emailInput = findViewById(R.id.text_input_email);
        usernameInput = findViewById(R.id.text_input_username);
        passwordInput = findViewById(R.id.text_input_password);
        confirmPasswordInput = findViewById(R.id.text_input_confirm_password);

        // Ambil data dari intent
        currentUsername = getIntent().getStringExtra("username");
        String currentEmail = getIntent().getStringExtra("email");
        userId = db.getUserId(currentUsername);
        
        // Set data saat ini ke EditText
        emailInput.setText(currentEmail);
        usernameInput.setText(currentUsername);

  

        // Setup tombol Save dan Cancel di bawah
        Button saveButton = findViewById(R.id.btn_save);
        Button cancelButton = findViewById(R.id.btn_cancel);

        saveButton.setOnClickListener(v -> updateProfile());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void updateProfile() {
        String newEmail = emailInput.getText().toString().trim();
        String newUsername = usernameInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validasi input
        if (newEmail.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi email dan username baru
        if (!newEmail.equals(db.getEmailByUserId(userId)) && db.isEmailExists(newEmail)) {
            Toast.makeText(this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newUsername.equals(currentUsername) && db.isUsernameExists(newUsername)) {
            Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data user
        if (db.updateUser(currentUsername, newUsername, newPassword)) {
            // Update SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", newUsername);
            editor.apply();

            new AlertDialog.Builder(this)
                .setTitle("Sukses")
                .setMessage("Profil berhasil diperbarui")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(EditProfile.this, ProfileSetting.class);
                    startActivity(intent);
                    finish();
                })
                .show();
        } else {
            Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
        }
    }
}
