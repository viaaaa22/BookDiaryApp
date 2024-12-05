package com.example.footer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    private DatabaseHandler dbHandler;
    private EditText emailInput, usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        
        // Inisialisasi DatabaseHandler
        dbHandler = new DatabaseHandler(this);
        
        // Inisialisasi EditText
        emailInput = findViewById(R.id.text_placeholder_email);
        usernameInput = findViewById(R.id.text_placeholder_username);
        passwordInput = findViewById(R.id.text_placeholder_password);
        
        // Inisialisasi button sign up
        Button btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
        
        // Inisialisasi button cancel
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman MainActivity
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                // Membersihkan stack activity sebelumnya
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    
    private void signUpUser() {
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        // Validasi input kosong
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Semua field harus diisi!");
            return;
        }
        
        // Validasi email sudah ada
        if (dbHandler.isEmailExists(email)) {
            showAlert("Email sudah terdaftar!");
            return;
        }
        
        // Validasi username sudah ada
        if (dbHandler.isUsernameExists(username)) {
            showAlert("Username sudah terdaftar!");
            return;
        }
        
        // Mencoba menambahkan user baru
        long result = dbHandler.addUser(email, username, password);
        
        if (result != -1) {
            // Berhasil signup
            Toast.makeText(SignUp.this, "Sign up berhasil!", Toast.LENGTH_SHORT).show();
            // Pindah ke halaman Login
            Intent intent = new Intent(SignUp.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // Gagal signup
            showAlert("Sign up gagal! Silakan coba lagi.");
        }
    }
    
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Peringatan")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
