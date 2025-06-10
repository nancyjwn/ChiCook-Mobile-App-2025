package com.example.chicook.layout.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.chicook.layout.fragment.BookmarkFragment;
import com.example.chicook.layout.fragment.HomeFragment;
import com.example.chicook.layout.fragment.NotesFragment;
import com.example.chicook.R;
import com.example.chicook.layout.fragment.SearchFragment;
import com.example.chicook.ThemeHelper;
import com.example.chicook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.add_book) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.favorites) {
                selectedFragment = new BookmarkFragment();
            } else if (id == R.id.community) {
                selectedFragment = new NotesFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.home) {
                item.setIcon(R.drawable.homeon);
                binding.bottomNavigation.getMenu().findItem(R.id.add_book).setIcon(R.drawable.searchoff);
                binding.bottomNavigation.getMenu().findItem(R.id.community).setIcon(R.drawable.notesoff);
                binding.bottomNavigation.getMenu().findItem(R.id.favorites).setIcon(R.drawable.bookmark_off);
                selectedFragment = new HomeFragment();
            } else if (id == R.id.add_book) {
                item.setIcon(R.drawable.searchon);
                binding.bottomNavigation.getMenu().findItem(R.id.home).setIcon(R.drawable.homeoff);
                binding.bottomNavigation.getMenu().findItem(R.id.community).setIcon(R.drawable.notesoff);
                binding.bottomNavigation.getMenu().findItem(R.id.favorites).setIcon(R.drawable.bookmark_off);
                selectedFragment = new SearchFragment();
            } else if (id == R.id.community) {
                item.setIcon(R.drawable.noteson);
                binding.bottomNavigation.getMenu().findItem(R.id.home).setIcon(R.drawable.homeoff);
                binding.bottomNavigation.getMenu().findItem(R.id.add_book).setIcon(R.drawable.searchoff);
                binding.bottomNavigation.getMenu().findItem(R.id.favorites).setIcon(R.drawable.bookmark_off);
                selectedFragment = new NotesFragment();
            } else if (id == R.id.favorites) {
                item.setIcon(R.drawable.bookmark_on);
                binding.bottomNavigation.getMenu().findItem(R.id.home).setIcon(R.drawable.homeoff);
                binding.bottomNavigation.getMenu().findItem(R.id.add_book).setIcon(R.drawable.searchoff);
                binding.bottomNavigation.getMenu().findItem(R.id.community).setIcon(R.drawable.notesoff);
                selectedFragment = new BookmarkFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}