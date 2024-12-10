package com.example.footer.ui.wishlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footer.DatabaseHandler;
import com.example.footer.R;
import com.example.footer.databinding.FragmentWishlistBinding;
import static android.content.Context.MODE_PRIVATE;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private DatabaseHandler db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                            ViewGroup container, Bundle savedInstanceState) {
        WishlistViewModel wishlistViewModel =
                new ViewModelProvider(this).get(WishlistViewModel.class);

        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = new DatabaseHandler(requireContext());

        // Setup tombol add
        View btnAddWishlist = root.findViewById(R.id.btn_add_wishlist);
        if (btnAddWishlist != null) {
            btnAddWishlist.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddWishlist.class);
                startActivity(intent);
            });
        }

        // Ambil data user
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        int userId = db.getUserId(username);

        // Update jumlah buku
        TextView countView = root.findViewById(R.id.text_wishlist_count);
        if (countView != null) {
            int wishlistCount = db.getWishlistBooksCount(userId);
            countView.setText(String.valueOf(wishlistCount));
            wishlistViewModel.setWishlistCount(wishlistCount);
        }

        loadWishlistBooks();

        return root;
    }

    private void loadWishlistBooks() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        int userId = db.getUserId(username);

        // Load buku-buku wishlist
        Cursor cursor = db.getWishlistBooksByUserId(userId);
        LinearLayout container = binding.getRoot().findViewById(R.id.container_books);
        if (container != null) {
            container.removeAllViews();

            while (cursor.moveToNext()) {
                View bookView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_wishlist_book, container, false);
                
                // Inisialisasi views
                TextView titleView = bookView.findViewById(R.id.book_title);
                TextView authorView = bookView.findViewById(R.id.book_author);
                TextView linkView = bookView.findViewById(R.id.book_link);
                ImageView coverView = bookView.findViewById(R.id.book_cover);

                // Ambil data dari cursor
                final int id = cursor.getInt(cursor.getColumnIndex("id_whistlist"));
                final String title = cursor.getString(cursor.getColumnIndex("title_whistlist"));
                final String author = cursor.getString(cursor.getColumnIndex("author_whistlist"));
                final String link = cursor.getString(cursor.getColumnIndex("link_whistlist"));
                final byte[] coverBytes = cursor.getBlob(cursor.getColumnIndex("cover_whistlist"));

                // Set data ke views
                titleView.setText(title);
                authorView.setText(author);
                linkView.setText(link);

                // Set cover image jika ada
                if (coverBytes != null && coverBytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                    coverView.setImageBitmap(bitmap);
                }

                // Optional: tambahkan onClick listener untuk membuka detail buku
                bookView.setOnClickListener(v -> {
                    // Tambahkan intent ke halaman detail wishlist jika diperlukan
                    Intent intent = new Intent(getActivity(), InfoWishlist.class);
                    intent.putExtra("id_wishlist", id);
                    intent.putExtra("title", title);
                    intent.putExtra("author", author);
                    intent.putExtra("link", link);
                    intent.putExtra("cover", coverBytes);
                    startActivity(intent);
                });

                container.addView(bookView);
            }
        }
        cursor.close();
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