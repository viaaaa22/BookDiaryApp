package com.example.footer.ui.wishlist;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.footer.DatabaseHandler;
import com.example.footer.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddWishlist extends AppCompatActivity {
    private EditText titleInput, authorInput, linkInput;
    private ImageView bookCoverImage;
    private byte[] coverImageBytes;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_wishlist);

        titleInput = findViewById(R.id.text_enter_title);
        authorInput = findViewById(R.id.text_enter_author);
        linkInput = findViewById(R.id.text_enter_link);
        bookCoverImage = findViewById(R.id.image_book_cover);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnChecklist = findViewById(R.id.btn_checklist);

        btnBack.setOnClickListener(v -> finish());

        bookCoverImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnChecklist.setOnClickListener(v -> validateAndSave());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK 
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bookCoverImage.setImageBitmap(bitmap);
                
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                coverImageBytes = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateAndSave() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String link = linkInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || link.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!URLUtil.isValidUrl(link)) {
            Toast.makeText(this, "Link harus berupa URL yang valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (coverImageBytes == null) {
            Toast.makeText(this, "Silakan pilih cover buku!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHandler dbHandler = new DatabaseHandler(this);
        ContentValues values = new ContentValues();
        values.put("title_whistlist", title);
        values.put("author_whistlist", author);
        values.put("link_whistlist", link);
        values.put("cover_whistlist", coverImageBytes);

        long result = dbHandler.getWritableDatabase().insert("whistlist", null, values);
        if (result != -1) {
            Toast.makeText(this, "Wishlist berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menambahkan wishlist!", Toast.LENGTH_SHORT).show();
        }
    }
}
