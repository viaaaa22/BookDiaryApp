package com.example.footer.ui.finished;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class AddFinished extends AppCompatActivity {
    private EditText titleInput, authorInput, dateInput, rateInput, summaryInput;
    private ImageView bookCoverImage;
    private byte[] coverImageBytes;
    private DatabaseHandler dbHandler;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_finished);

        dbHandler = new DatabaseHandler(this);

        // Inisialisasi views
        titleInput = findViewById(R.id.textfield_text);
        authorInput = findViewById(R.id.author_textfield_text);
        dateInput = findViewById(R.id.finished_date_textfield_text);
        rateInput = findViewById(R.id.rate_textfield_text);
        summaryInput = findViewById(R.id.summary_textfield_text);
        bookCoverImage = findViewById(R.id.book_cover_image);

        // Setup DatePicker untuk finished date
        dateInput.setOnClickListener(v -> showDatePickerDialog());

        // Setup image picker untuk cover book
        bookCoverImage.setOnClickListener(v -> openFileChooser());

        // Setup tombol back
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Setup tombol save
        findViewById(R.id.btn_save).setOnClickListener(v -> validateAndSave());

        // Validasi input rate
        rateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    float rate = Float.parseFloat(s.toString());
                    if (rate > 5.0f) {
                        rateInput.setText("5.0");
                        rateInput.setSelection(rateInput.length());
                    } else if (rate < 0.0f) {
                        rateInput.setText("0.0");
                        rateInput.setSelection(rateInput.length());
                    }
                }
            }
        });
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
                    dateInput.setText(date);
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
                bookCoverImage.setImageBitmap(bitmap);
                
                // Convert bitmap to byte array
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
        String date = dateInput.getText().toString().trim();
        String rateStr = rateInput.getText().toString().trim();
        String summary = summaryInput.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || date.isEmpty() || 
            rateStr.isEmpty() || summary.isEmpty() || coverImageBytes == null) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        float rate = Float.parseFloat(rateStr);
        if (rate < 0 || rate > 5) {
            Toast.makeText(this, "Rating harus antara 0-5!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan ke database
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title_finished", title);
        values.put("author_finished", author);
        values.put("date_finished", date);
        values.put("rate", rate);
        values.put("summary", summary);
        values.put("cover_finished", coverImageBytes);
        
        long result = db.insert("finished", null, values);
        
        if (result != -1) {
            Toast.makeText(this, "Buku berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menambahkan buku!", Toast.LENGTH_SHORT).show();
        }
    }
}
