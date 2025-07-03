package com.example.moviet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE = "arg_movie";

    private Movie movie;

    private ImageView imgPosterLarge;
    private TextView tvMovieTitle, tvMovieGenres;
    private Button btnBuyTickets;

    // Static factory method to create fragment instance with Movie object
    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve Movie object from arguments bundle
        if (getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(ARG_MOVIE);
            if (serializable instanceof Movie) {
                movie = (Movie) serializable;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Find views by ID
        imgPosterLarge = view.findViewById(R.id.imgPosterLarge);
        tvMovieTitle = view.findViewById(R.id.tvMovieTitle);
        tvMovieGenres = view.findViewById(R.id.tvMovieGenres);
        btnBuyTickets = view.findViewById(R.id.btnBuyTickets);

        if (movie != null) {
            // Set movie title and genres text
            tvMovieTitle.setText(movie.getTitle());
            tvMovieGenres.setText(movie.getGenre() + " | " + movie.getDuration());

            // Load movie poster from Firebase Storage using Glide
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(movie.getImageurl());
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(requireContext())
                                .load(uri)
                                .placeholder(R.drawable.poster_placeholder)
                                .error(R.drawable.image_error)
                                .into(imgPosterLarge);
                    })
                    .addOnFailureListener(e -> imgPosterLarge.setImageResource(R.drawable.image_error));

            // Handle Buy Tickets button click (add your navigation or logic here)
            btnBuyTickets.setOnClickListener(v -> {
                // TODO: Implement ticket buying logic or navigation here
            });
        }

        return view;
    }
}
