package com.example.footer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private DatabaseHandler dbHandler;
    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        // Inisialisasi DatabaseHandler
        dbHandler = new DatabaseHandler(this);
        
        // Inisialisasi EditText
        usernameInput = findViewById(R.id.text_input);
        passwordInput = findViewById(R.id.text_input_password_textfield_text);
        
        // Inisialisasi button login
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        
        // Inisialisasi button signup
        Button btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman SignUp
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
    
    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Username dan password harus diisi!");
            return;
        }
        
        // Cek login
        if (dbHandler.checkLogin(username, password)) {
            // Login berhasil
            Toast.makeText(Login.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
            
            // Simpan ID user ke SharedPreferences untuk digunakan di halaman lain
            int userId = dbHandler.getUserId(username);
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("userId", userId);
            editor.putString("username", username);
            editor.apply();
            
            // Pindah ke NavButton activity yang berisi Profile, Finished, dan Wishlist
            Intent intent = new Intent(Login.this, NavButton.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // Login gagal
            showAlert("Username atau password salah!");
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
