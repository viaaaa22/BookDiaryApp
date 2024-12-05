package com.example.footer.ui.finished;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;
import com.example.footer.databinding.FragmentFinishedBinding;

public class FinishedFragment extends Fragment {

    private FragmentFinishedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        FinishedViewModel finishedViewModel =
                new ViewModelProvider(this).get(FinishedViewModel.class);

        binding = FragmentFinishedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAddWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFinished.class);
                startActivity(intent);
            }
        });

        loadFinishedBooks();

        return root;
    }

    private void loadFinishedBooks() {
        SQLiteDatabase db = new DatabaseHandler(getContext()).getReadableDatabase();
        Cursor cursor = db.query("finished", null, null, null, null, null, null);
        
        LinearLayout container = binding.getRoot().findViewById(R.id.container_books);
        container.removeAllViews();

        while (cursor.moveToNext()) {
            View bookView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_finished_book, container, false);
            
            // Set data ke views
            TextView titleView = bookView.findViewById(R.id.book_title);
            TextView authorView = bookView.findViewById(R.id.book_author);
            TextView dateView = bookView.findViewById(R.id.finished_date);
            RatingBar ratingBar = bookView.findViewById(R.id.rating_bar);
            TextView summaryView = bookView.findViewById(R.id.book_summary);
            ImageView coverView = bookView.findViewById(R.id.book_cover);

            // Simpan data saat ini dalam variabel final
            final int id = cursor.getInt(cursor.getColumnIndex("id_finished"));
            final String title = cursor.getString(cursor.getColumnIndex("title_finished"));
            final String author = cursor.getString(cursor.getColumnIndex("author_finished"));
            final String date = cursor.getString(cursor.getColumnIndex("date_finished"));
            final float rate = cursor.getFloat(cursor.getColumnIndex("rate"));
            final String summary = cursor.getString(cursor.getColumnIndex("summary"));
            final byte[] coverBytes = cursor.getBlob(cursor.getColumnIndex("cover_finished"));

            titleView.setText(title);
            authorView.setText(author);
            dateView.setText(date);
            ratingBar.setRating(rate);
            summaryView.setText(summary);

            if (coverBytes != null && coverBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                coverView.setImageBitmap(bitmap);
            }

            // Tambahkan onClick listener
            bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), InfoFinished.class);
                    intent.putExtra("id_finished", id);
                    intent.putExtra("title", title);
                    intent.putExtra("author", author);
                    intent.putExtra("date", date);
                    intent.putExtra("rate", rate);
                    intent.putExtra("summary", summary);
                    intent.putExtra("cover", coverBytes);
                    startActivity(intent);
                }
            });
            
            container.addView(bookView);
        }
        
        cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFinishedBooks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}