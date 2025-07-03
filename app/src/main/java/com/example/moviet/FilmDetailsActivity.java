package com.example.moviet;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FilmDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FilmDetailsActivity";
    private static final String COLLECTION = "Movie";

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private PlayerView playerView;
    private ExoPlayer player;

    private ImageView imagePoster;
    private TextView textTitle, textGenres, textRating, textDescription;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.film_details);

        setupWindowInsets();
        initViews();

        String documentId = getIntent().getStringExtra("documentId");

        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "No movie ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMovieDetails(documentId);

        btnBuy.setOnClickListener(v ->
                Toast.makeText(this, "Buying ticket…", Toast.LENGTH_SHORT).show());
    }

    private void setupWindowInsets() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void initViews() {
        playerView = findViewById(R.id.playerView);
        imagePoster = findViewById(R.id.imagePoster);
        textTitle = findViewById(R.id.textTitle);
        textGenres = findViewById(R.id.textGenres);
        textRating = findViewById(R.id.textRating);
        textDescription = findViewById(R.id.textDescription);
        btnBuy = findViewById(R.id.btnBuy);
    }

    private void loadMovieDetails(String documentId) {
        firestore.collection(COLLECTION).document(documentId)
                .get()
                .addOnSuccessListener(this::populateScreen)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore fetch failed", e);
                    Toast.makeText(this, "Failed to load film.", Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void populateScreen(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(this, "Film not found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String title = doc.getString("title");
        String genre = doc.getString("genre");
        String duration = doc.getString("duration");
        String imdb = doc.getString("IMDb");
        String description = doc.getString("description");
        String imagePath = doc.getString("imageurl");
        String trailerPath = doc.getString("trailerPath");

        textTitle.setText(title != null ? title : "N/A");
        textGenres.setText((genre != null ? genre : "Genre") + " • " + (duration != null ? duration : "Duration"));
        textRating.setText("IMDb ★ " + (imdb != null ? imdb : "0.0"));
        textDescription.setText(description != null ? description : "No description available.");

        loadPosterImage(imagePath);
        loadTrailer(trailerPath);
    }

    private void loadPosterImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            storage.getReference(imagePath).getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.poster_placeholder)
                            .error(R.drawable.image_error)
                            .into(imagePoster))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Poster load failed", e);
                        imagePoster.setImageResource(R.drawable.image_error);
                    });
        } else {
            imagePoster.setImageResource(R.drawable.image_error);
        }
    }

    private void loadTrailer(String trailerPath) {
        if (trailerPath != null && !trailerPath.isEmpty()) {
            storage.getReference(trailerPath).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        player = new ExoPlayer.Builder(this).build();
                        playerView.setPlayer(player);
                        player.setMediaItem(MediaItem.fromUri(uri));
                        player.prepare();
                        player.setPlayWhenReady(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Trailer load failed", e);
                        hidePlayer();
                    });
        } else {
            hidePlayer();
        }
    }

    private void hidePlayer() {
        playerView.setUseController(false);
        playerView.setAlpha(0f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
