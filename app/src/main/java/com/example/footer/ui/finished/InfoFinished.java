package com.example.footer.ui.finished;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;

public class InfoFinished extends AppCompatActivity {
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_finished);

        // Ambil data dari intent
        bookId = getIntent().getIntExtra("id_finished", -1);
        String title = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String date = getIntent().getStringExtra("date");
        float rate = getIntent().getFloatExtra("rate", 0);
        String summary = getIntent().getStringExtra("summary");
        byte[] coverBytes = getIntent().getByteArrayExtra("cover");

        // Set data ke views
        TextView titleView = findViewById(R.id.book_title);
        TextView authorView = findViewById(R.id.book_author);
        TextView dateView = findViewById(R.id.finished_date);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        TextView summaryView = findViewById(R.id.book_summary);
        ImageView coverView = findViewById(R.id.book_cover);

        titleView.setText(title);
        authorView.setText(author);
        dateView.setText(date);
        ratingBar.setRating(rate);
        summaryView.setText(summary);

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
            Intent intent = new Intent(InfoFinished.this, EditFinished.class);
            intent.putExtra("id_finished", bookId);
            startActivity(intent);
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus buku ini?")
            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteBook();
                }
            })
            .setNegativeButton("Tidak", null)
            .show();
    }

    private void deleteBook() {
        SQLiteDatabase db = new DatabaseHandler(this).getWritableDatabase();
        db.delete("finished", "id_finished = ?", new String[]{String.valueOf(bookId)});
        finish();
    }
}