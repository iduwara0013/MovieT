package com.example.moviet;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SubmissionFormActivity extends AppCompatActivity {

    private TextView tvMovieName, tvUsername, tvDate, tvTime, tvSeats, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_form);

        tvMovieName = findViewById(R.id.tvMovieName);
        tvUsername = findViewById(R.id.tvUsername);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvSeats = findViewById(R.id.tvSeats);
        tvTotal = findViewById(R.id.tvTotal);

        String docId = getIntent().getStringExtra("docId");
        if (docId == null) {
            Toast.makeText(this, "No booking info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchBookingInfo(docId);
    }

    private void fetchBookingInfo(String docId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("SeatSelection").document(docId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String movieName = documentSnapshot.getString("movieTitle");
                String username = documentSnapshot.getString("username");
                String date = documentSnapshot.getString("date");
                String time = documentSnapshot.getString("time");
                List<String> seats = (List<String>) documentSnapshot.get("selectedSeats");
                Long totalPrice = documentSnapshot.getLong("totalPrice");

                tvMovieName.setText("Movie Name: " + movieName);
                tvUsername.setText("Username: " + username);
                tvDate.setText("Date: " + date);
                tvTime.setText("Time: " + time);
                tvSeats.setText("Selected Seats: " + seats);
                tvTotal.setText("Total Price: " + totalPrice + " LKR");
            } else {
                Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading booking info", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
