package com.example.moviet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FilmActivity extends AppCompatActivity {

    TextView tvMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_activity);

        tvMovieTitle = findViewById(R.id.tvMovieTitle);

        String movieTitle = getIntent().getStringExtra("movieTitle");

        if (movieTitle != null) {
            tvMovieTitle.setText("Movie: " + movieTitle);
        } else {
            tvMovieTitle.setText("Movie title not found.");
        }
    }
}
