package com.example.moviet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final List<Movie> movieList = new ArrayList<>();
    private final List<Movie> nowShowingList = new ArrayList<>();
    private final List<Movie> comingSoonList = new ArrayList<>();

    private MovieAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Apply edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Optional toolbar click
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> {
                // Handle toolbar click if needed
            });
        }

        // Setup Tabs
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Now Showing"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Coming Soon"));

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Init Firestore
        db = FirebaseFirestore.getInstance();

        // Load movies
        loadMovies();

        // Tab Listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterMovies(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    /** Load movies from Firestore */
    private void loadMovies() {
        db.collection("Movie")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    movieList.clear();
                    nowShowingList.clear();
                    comingSoonList.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            // 🔷 FIX: Set documentId so FilmDetailsActivity works correctly
                            movie.setDocumentId(doc.getId());
                            movieList.add(movie);

                            String status = movie.getStatus();
                            if ("now_showing".equalsIgnoreCase(status)) {
                                nowShowingList.add(movie);
                            } else if ("coming_soon".equalsIgnoreCase(status)) {
                                comingSoonList.add(movie);
                            }
                        }
                    }

                    // Show default tab (Now Showing)
                    adapter.updateList(nowShowingList);

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch movies from Firestore", e);
                });
    }

    /** Filter movies based on tab selected */
    private void filterMovies(int tabPosition) {
        if (tabPosition == 0) {
            adapter.updateList(nowShowingList);
        } else if (tabPosition == 1) {
            adapter.updateList(comingSoonList);
        }
    }
}
