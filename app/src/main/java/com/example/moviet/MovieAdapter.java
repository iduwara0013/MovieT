package com.example.moviet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

/**
 * RecyclerView adapter to display movie posters and details.
 */
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
        View v = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVH holder, int position) {
        Movie movie = movies.get(position);

        // Set text views with movie details
        holder.tvTitle.setText(movie.getTitle());
        holder.tvGenreDuration.setText(movie.getGenre() + "\n" + movie.getDuration());
        holder.tvRating.setText(String.valueOf(movie.getImdb()));

        // Load movie poster image from Firebase Storage
        String imagePath = movie.getImageurl();
        Log.d("MovieAdapter", "Loading imagePath: " + imagePath);

        if (imagePath != null && !imagePath.isEmpty()) {
            storage.getReference()
                    .child(imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Log.d("MovieAdapter", "Image URI: " + uri.toString());
                        Glide.with(context)
                                .load(uri)
                                .placeholder(R.drawable.poster_placeholder)
                                .error(R.drawable.image_error)
                                .into(holder.imgPoster);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MovieAdapter", "Failed to load image for " + imagePath, e);
                        holder.imgPoster.setImageResource(R.drawable.image_error);
                    });
        } else {
            Log.w("MovieAdapter", "imagePath is null or empty for movie: " + movie.getTitle());
            holder.imgPoster.setImageResource(R.drawable.image_error);
        }

        // Buy button click listener (optional)
        holder.btnBuy.setOnClickListener(view -> {
            // TODO: Implement ticket booking navigation or action here
            // Example: Toast.makeText(context, "Buying ticket for: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Update the adapter list and refresh the RecyclerView.
     */
    public void updateList(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    /* ViewHolder class */
    static class MovieVH extends RecyclerView.ViewHolder {
        ImageView imgPoster, imgStar;
        TextView tvTitle, tvGenreDuration, tvRating;
        MaterialButton btnBuy;

        MovieVH(@NonNull View itemView) {
            super(itemView);
            imgPoster       = itemView.findViewById(R.id.imgPoster);
            imgStar         = itemView.findViewById(R.id.imgStar);
            tvTitle         = itemView.findViewById(R.id.tvTitle);
            tvGenreDuration = itemView.findViewById(R.id.tvGenreDuration);
            tvRating        = itemView.findViewById(R.id.tvRating);
            btnBuy          = itemView.findViewById(R.id.btnBuy);
        }
    }
}
