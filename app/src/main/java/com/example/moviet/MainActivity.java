package com.example.moviet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Movie> movieList = new ArrayList<>();         // All movies from Firestore
    private final List<Movie> nowShowingList = new ArrayList<>();     // Filtered list for Now Showing
    private final List<Movie> comingSoonList = new ArrayList<>();     // Filtered list for Coming Soon

    private MovieAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar click dummy
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> {
                // TODO: Open drawer or menu
            });
        }

        // Tabs setup
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.addTab(tabs.newTab().setText("Now Showing"), true);
        tabs.addTab(tabs.newTab().setText("Coming Soon"));

        // RecyclerView setup
        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, new ArrayList<>()); // start empty list
        rv.setAdapter(adapter);

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        // Load movies from Firestore
        loadMovies();

        // Tab selection listener to filter movies
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterMoviesByTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // no action
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // no action
            }
        });
    }

    // Load movies from Firestore once
    private void loadMovies() {
        db.collection("Movie")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    movieList.clear();
                    nowShowingList.clear();
                    comingSoonList.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        Movie m = doc.toObject(Movie.class);
                        if (m != null) {
                            movieList.add(m);
                            // Separate into lists by status
                            if ("now_showing".equalsIgnoreCase(m.getStatus())) {
                                nowShowingList.add(m);
                            } else if ("coming_soon".equalsIgnoreCase(m.getStatus())) {
                                comingSoonList.add(m);
                            }
                        }
                    }

                    // By default show "Now Showing" movies in adapter
                    adapter.updateList(nowShowingList);
                })
                .addOnFailureListener(e -> Log.e("MainActivity", "Firestore error: ", e));
    }

    // Filter movies based on selected tab position
    private void filterMoviesByTab(int tabPosition) {
        if (tabPosition == 0) {
            adapter.updateList(nowShowingList);
        } else if (tabPosition == 1) {
            adapter.updateList(comingSoonList);
        }
    }
}
