package com.example.footer.ui.wishlist;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;

public class InfoWishlist extends AppCompatActivity {
    private DatabaseHandler dbHandler;
    private int wishlistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_wishlist);

        dbHandler = new DatabaseHandler(this);
        wishlistId = getIntent().getIntExtra("wishlist_id", -1);

        // Inisialisasi views
        ImageView coverView = findViewById(R.id.book_cover_info);
        TextView titleView = findViewById(R.id.book_title_info);
        TextView authorView = findViewById(R.id.book_author_info);
        TextView linkView = findViewById(R.id.book_link_info);
        
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnEdit = findViewById(R.id.btn_edit);

        // Ambil data dari database
        loadWishlistData(wishlistId, coverView, titleView, authorView, linkView);

        // Setup button listeners
        btnCancel.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(InfoWishlist.this, EditWishlist.class);
            intent.putExtra("wishlist_id", wishlistId);
            startActivity(intent);
        });
    }

    private void loadWishlistData(int wishlistId, ImageView coverView, TextView titleView, 
                                TextView authorView, TextView linkView) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String[] columns = {"title_whistlist", "author_whistlist", "cover_whistlist", "link_whistlist"};
        String selection = "id_whistlist=?";
        String[] selectionArgs = {String.valueOf(wishlistId)};
        
        Cursor cursor = db.query("whistlist", columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            titleView.setText(cursor.getString(cursor.getColumnIndex("title_whistlist")));
            authorView.setText(cursor.getString(cursor.getColumnIndex("author_whistlist")));
            linkView.setText(cursor.getString(cursor.getColumnIndex("link_whistlist")));

            byte[] coverBytes = cursor.getBlob(cursor.getColumnIndex("cover_whistlist"));
            if (coverBytes != null && coverBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                coverView.setImageBitmap(bitmap);
            }
        }
        cursor.close();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus buku ini dari wishlist?")
                .setPositiveButton("Ya", (dialog, which) -> deleteWishlistItem())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteWishlistItem() {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        String whereClause = "id_whistlist=?";
        String[] whereArgs = {String.valueOf(wishlistId)};
        
        int deletedRows = db.delete("whistlist", whereClause, whereArgs);
        
        if (deletedRows > 0) {
            Toast.makeText(this, "Buku berhasil dihapus dari wishlist", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menghapus buku", Toast.LENGTH_SHORT).show();
        }
    }
}
