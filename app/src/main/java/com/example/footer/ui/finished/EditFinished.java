package com.example.footer.ui.finished;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.NavButton;
import com.example.footer.R;

public class EditFinished extends AppCompatActivity {
    private int bookId;
    private EditText titleEdit, authorEdit, dateEdit, summaryEdit;
    private RatingBar ratingBar;
    private ImageView coverImage;
    private byte[] coverImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_finished);

        // Inisialisasi views
        titleEdit = findViewById(R.id.input_textfield_text);
        authorEdit = findViewById(R.id.input_textfield_text1);
        dateEdit = findViewById(R.id.input_textfield_text2);
        ratingBar = findViewById(R.id.input_textfield_text3);
        summaryEdit = findViewById(R.id.input_textfield_text4);
        coverImage = findViewById(R.id.book_cover_image);

        // Ambil ID buku dari intent
        bookId = getIntent().getIntExtra("id_finished", -1);
        
        // Load data buku
        loadBookData();

        // Setup buttons
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnSave = findViewById(R.id.btn_save);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void loadBookData() {
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        
        Cursor cursor = db.query("finished", null, 
            "id_finished = ?", new String[]{String.valueOf(bookId)}, 
            null, null, null);

        if (cursor.moveToFirst()) {
            titleEdit.setText(cursor.getString(cursor.getColumnIndex("title_finished")));
            authorEdit.setText(cursor.getString(cursor.getColumnIndex("author_finished")));
            dateEdit.setText(cursor.getString(cursor.getColumnIndex("date_finished")));
            ratingBar.setRating(cursor.getFloat(cursor.getColumnIndex("rate")));
            summaryEdit.setText(cursor.getString(cursor.getColumnIndex("summary")));
            
            coverImageBytes = cursor.getBlob(cursor.getColumnIndex("cover_finished"));
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
        values.put("title_finished", titleEdit.getText().toString());
        values.put("author_finished", authorEdit.getText().toString());
        values.put("date_finished", dateEdit.getText().toString());
        values.put("rate", ratingBar.getRating());
        values.put("summary", summaryEdit.getText().toString());
        if (coverImageBytes != null) {
            values.put("cover_finished", coverImageBytes);
        }
    
        int rowsAffected = db.update("finished", values, 
            "id_finished = ?", new String[]{String.valueOf(bookId)});
    
        if (rowsAffected > 0) {
            new AlertDialog.Builder(this)
                .setTitle("Sukses")
                .setMessage("Data berhasil diperbarui")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Kembali ke NavButton (MainActivity)
                    Intent intent = new Intent(EditFinished.this, NavButton.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
        }
    
        db.close();
    }
}
