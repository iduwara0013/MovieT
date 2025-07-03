package com.example.moviet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieVH> {

    private final List<Movie> movies;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final Context context;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public MovieVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVH holder, int position) {
        Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvGenreDuration.setText(movie.getGenre() + "\n" + movie.getDuration());
        holder.tvRating.setText(String.valueOf(movie.getIMDb()));

        String imagePath = movie.getImageurl();

        if (imagePath != null && !imagePath.isEmpty()) {
            storage.getReference()
                    .child(imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.poster_placeholder)
                            .error(R.drawable.image_error)
                            .into(holder.imgPoster))
                    .addOnFailureListener(e -> holder.imgPoster.setImageResource(R.drawable.image_error));
        } else {
            holder.imgPoster.setImageResource(R.drawable.image_error);
        }

        // Buy Ticket button click listener
        holder.btnBuy.setOnClickListener(v -> handleBuyTicket(movie.getTitle()));

        // Navigate to FilmDetailsActivity when movie title is clicked
        holder.tvTitle.setOnClickListener(v -> {
            Intent intent = new Intent(context, FilmDetailsActivity.class);
            // Pass document ID or relevant unique ID for detailed loading
            intent.putExtra("documentId", movie.getDocumentId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateList(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    private void handleBuyTicket(String movieTitle) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null && !username.isEmpty()) {
            prefs.edit().putString("movieTitle", movieTitle).apply();
            Intent intent = new Intent(context, SeatSelectionActivity.class);
            intent.putExtra("movieTitle", movieTitle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Login Required")
                    .setMessage("You are not logged in. Please log in to continue.")
                    .setPositiveButton("Login", (dialog, which) -> {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Register", (dialog, which) -> {
                        Intent intent = new Intent(context, RegisterActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
        }
    }

    static class MovieVH extends RecyclerView.ViewHolder {
        ImageView imgPoster, imgStar;
        TextView tvTitle, tvGenreDuration, tvRating;
        MaterialButton btnBuy;

        MovieVH(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            imgStar = itemView.findViewById(R.id.imgStar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenreDuration = itemView.findViewById(R.id.tvGenreDuration);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
