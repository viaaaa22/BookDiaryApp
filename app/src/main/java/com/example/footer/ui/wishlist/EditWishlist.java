package com.example.footer.ui.wishlist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.NavButton;
import com.example.footer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditWishlist extends AppCompatActivity {
    private int wishlistId;
    private EditText titleEdit, authorEdit, linkEdit;
    private ImageView coverImage;
    private byte[] coverImageBytes;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_wishlist);

        // Inisialisasi views
        titleEdit = findViewById(R.id.text_input_title_description);
        authorEdit = findViewById(R.id.text_input_author_description);
        linkEdit = findViewById(R.id.text_input_link_description);
        coverImage = findViewById(R.id.image_book_cover);

        // Ambil ID buku dari intent
        wishlistId = getIntent().getIntExtra("id_wishlist", -1);
        
        // Load data buku
        loadBookData();

        // Setup buttons
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnSave = findViewById(R.id.btn_save);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
        
        // Setup image picker
        coverImage.setOnClickListener(v -> openFileChooser());
    }

    private void loadBookData() {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        
        Cursor cursor = db.query("whistlist", null, 
            "id_whistlist = ?", new String[]{String.valueOf(wishlistId)}, 
            null, null, null);

        if (cursor.moveToFirst()) {
            titleEdit.setText(cursor.getString(cursor.getColumnIndex("title_whistlist")));
            authorEdit.setText(cursor.getString(cursor.getColumnIndex("author_whistlist")));
            linkEdit.setText(cursor.getString(cursor.getColumnIndex("link_whistlist")));
            
            coverImageBytes = cursor.getBlob(cursor.getColumnIndex("cover_whistlist"));
            if (coverImageBytes != null && coverImageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(coverImageBytes, 0, coverImageBytes.length);
                coverImage.setImageBitmap(bitmap);
            }
        }
        
        cursor.close();
        db.close();
    }

    private void saveChanges() {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
    
        ContentValues values = new ContentValues();
        values.put("title_whistlist", titleEdit.getText().toString());
        values.put("author_whistlist", authorEdit.getText().toString());
        values.put("link_whistlist", linkEdit.getText().toString());
        if (coverImageBytes != null) {
            values.put("cover_whistlist", coverImageBytes);
        }
    
        int rowsAffected = db.update("whistlist", values, 
            "id_whistlist = ?", new String[]{String.valueOf(wishlistId)});
    
        if (rowsAffected > 0) {
            new AlertDialog.Builder(this)
                .setTitle("Sukses")
                .setMessage("Data berhasil diperbarui")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(EditWishlist.this, NavButton.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
        }
    
        db.close();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK 
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                coverImage.setImageBitmap(bitmap);
                
                // Convert bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                coverImageBytes = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
