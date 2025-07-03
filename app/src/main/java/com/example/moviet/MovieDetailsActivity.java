package com.example.moviet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private ViewPager2 viewPagerMovies;
    private ImageView arrowUp;

    private List<Movie> movieList = new ArrayList<>();
    private MoviePagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        viewPagerMovies = findViewById(R.id.viewPagerMovies);
        arrowUp = findViewById(R.id.arrowUp);

        // Set arrow click listener to navigate back to MainActivity
        arrowUp.setOnClickListener(v -> navigateToMain());

        // Load all movies from Firestore and set up ViewPager2
        loadAllMovies();
    }

    private void loadAllMovies() {
        FirebaseFirestore.getInstance().collection("Movie")
                .get()
                .addOnSuccessListener(this::onMoviesLoaded)
                .addOnFailureListener(e -> {
                    // Handle errors here, e.g. show a Toast or log the error
                    e.printStackTrace();
                });
    }

    private void onMoviesLoaded(QuerySnapshot querySnapshot) {
        if (querySnapshot == null || querySnapshot.isEmpty()) {
            // Handle empty case if needed
            return;
        }
        movieList.clear();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Movie movie = doc.toObject(Movie.class);
            if (movie != null) {
                // Optionally set document ID to your model
                movie.setDocumentId(doc.getId());
                movieList.add(movie);
            }
        }
        adapter = new MoviePagerAdapter(this, movieList);
        viewPagerMovies.setAdapter(adapter);
    }

    private void navigateToMain() {
        Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
