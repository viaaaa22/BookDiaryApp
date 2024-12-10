package com.example.footer.ui.wishlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;

public class InfoWishlist extends AppCompatActivity {
    private int wishlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_wishlist);

        // Ambil data dari intent
        wishlistId = getIntent().getIntExtra("id_wishlist", -1);
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String link = getIntent().getStringExtra("link");
        byte[] coverBytes = getIntent().getByteArrayExtra("cover");

        // Set data ke views
        TextView titleView = findViewById(R.id.book_title_info);
        TextView authorView = findViewById(R.id.book_author_info);
        TextView linkView = findViewById(R.id.book_link_info);
        ImageView coverView = findViewById(R.id.book_cover_info);

        titleView.setText(title);
        authorView.setText(author);
        linkView.setText(link);

        if (coverBytes != null && coverBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
            coverView.setImageBitmap(bitmap);
        }

        // Setup buttons
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnEdit = findViewById(R.id.btn_edit);

        btnCancel.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(InfoWishlist.this, EditWishlist.class);
            intent.putExtra("id_wishlist", wishlistId);
            startActivity(intent);
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus buku ini dari wishlist?")
            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteWishlistBook();
                }
            })
            .setNegativeButton("Tidak", null)
            .show();
    }

    private void deleteWishlistBook() {
        SQLiteDatabase db = new DatabaseHandler(this).getWritableDatabase();
        db.delete("whistlist", "id_whistlist = ?", new String[]{String.valueOf(wishlistId)});
        finish();
    }
}
