package com.example.footer.ui.finished;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Calendar;
import java.util.Locale;

public class EditFinished extends AppCompatActivity {
    private int bookId;
    private EditText titleEdit, authorEdit, dateEdit, summaryEdit, rateEdit;
    private ImageView coverImage;
    private byte[] coverImageBytes;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_finished);

        // Inisialisasi views
        titleEdit = findViewById(R.id.input_textfield_text);
        authorEdit = findViewById(R.id.input_textfield_text1);
        dateEdit = findViewById(R.id.input_textfield_text2);
        rateEdit = findViewById(R.id.input_textfield_text3);
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

        // Setup DatePicker
        dateEdit.setOnClickListener(v -> showDatePickerDialog());
        
        // Setup image picker
        coverImage.setOnClickListener(v -> openFileChooser());
        
        // Setup validasi rate
        rateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        float rate = Float.parseFloat(s.toString());
                        if (rate > 5.0f) {
                            rateEdit.setText("5.0");
                            rateEdit.setSelection(rateEdit.length());
                        } else if (rate < 0.0f) {
                            rateEdit.setText("0.0");
                            rateEdit.setSelection(rateEdit.length());
                        }
                    } catch (NumberFormatException e) {
                        rateEdit.setText("0.0");
                        rateEdit.setSelection(rateEdit.length());
                    }
                }
            }
        });
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
            rateEdit.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndex("rate"))));
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
        values.put("rate", Float.parseFloat(rateEdit.getText().toString()));
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

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%d", 
                        dayOfMonth, monthOfYear + 1, year1);
                    dateEdit.setText(date);
                }, year, month, day);
        datePickerDialog.show();
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
