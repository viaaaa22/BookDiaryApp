package com.example.footer.ui.wishlist;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;
import com.example.footer.databinding.FragmentWishlistBinding;

public class WishlistFragment extends Fragment {
    private FragmentWishlistBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        WishlistViewModel wishlistViewModel =
                new ViewModelProvider(this).get(WishlistViewModel.class);

        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnAddWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddWishlist.class);
            startActivity(intent);
        });

        loadWishlistBooks();

        return root;
    }

    private void loadWishlistBooks() {
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Cursor cursor = db.query("whistlist", null, null, null, null, null, null);

        LinearLayout container = binding.getRoot().findViewById(R.id.container_books);
        container.removeAllViews();

        try {
            while (cursor.moveToNext()) {
                View bookView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_wishlist_book, container, false);

                TextView titleView = bookView.findViewById(R.id.book_title);
                TextView authorView = bookView.findViewById(R.id.book_author);
                TextView linkView = bookView.findViewById(R.id.book_link);
                ImageView coverView = bookView.findViewById(R.id.book_cover);

                int idColumnIndex = cursor.getColumnIndex("id_whistlist");
                int titleColumnIndex = cursor.getColumnIndex("title_whistlist");
                int authorColumnIndex = cursor.getColumnIndex("author_whistlist");
                int linkColumnIndex = cursor.getColumnIndex("link_whistlist");
                int coverColumnIndex = cursor.getColumnIndex("cover_whistlist");

                final int wishlistId = cursor.getInt(idColumnIndex);
                titleView.setText(cursor.getString(titleColumnIndex));
                authorView.setText(cursor.getString(authorColumnIndex));
                linkView.setText(cursor.getString(linkColumnIndex));

                byte[] coverBytes = cursor.getBlob(coverColumnIndex);
                if (coverBytes != null && coverBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                    coverView.setImageBitmap(bitmap);
                }

                bookView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), InfoWishlist.class);
                    intent.putExtra("wishlist_id", wishlistId);
                    startActivity(intent);
                });

                container.addView(bookView);
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWishlistBooks();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}