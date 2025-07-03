package com.example.moviet;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SeatSelectionActivity extends AppCompatActivity {

    private GridLayout seatGrid;
    private TextView tvMovieName, tvSelectedSeats, tvTotalPrice;
    private Spinner spinnerTime;
    private Button btnSelectDate, btnBookSeats;

    private final List<String> selectedSeats = new ArrayList<>();
    private final Set<String> unavailableSeats = new HashSet<>();

    private FirebaseFirestore db;
    private String username;
    private String movieTitle;
    private String docId;
    private String selectedDate;
    private static final int SEAT_PRICE = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        seatGrid = findViewById(R.id.seatGrid);
        btnBookSeats = findViewById(R.id.btnBookSeats);
        tvMovieName = findViewById(R.id.tvMovieName);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        db = FirebaseFirestore.getInstance();

        username = getUsername();
        movieTitle = getMovieTitle();
        tvMovieName.setText(movieTitle);

        setupTimeSpinner();
        setupDatePicker();

        btnBookSeats.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
                return;
            }
            saveBookedSeats();
        });
    }

    private void setupTimeSpinner() {
        String[] times = {"4:30 PM", "6:30 PM", "10:00 PM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedDate != null) {
                    reloadSeats();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDatePicker() {
        btnSelectDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedDate = sdf.format(calendar.getTime());
                        btnSelectDate.setText("Date: " + selectedDate);

                        reloadSeats();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
    }

    private void reloadSeats() {
        if (selectedDate == null) return;

        String selectedTime = spinnerTime.getSelectedItem().toString();
        docId = username + "_" + movieTitle + "_" + selectedDate + "_" + selectedTime;

        unavailableSeats.clear();
        selectedSeats.clear();
        seatGrid.removeAllViews();

        DocumentReference docRef = db.collection("SeatSelection").document(docId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> bookedSeats = (List<String>) documentSnapshot.get("selectedSeats");
                if (bookedSeats != null) {
                    unavailableSeats.addAll(bookedSeats);
                }
            }
            generateSeats();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load booked seats", Toast.LENGTH_SHORT).show();
            generateSeats();
        });
    }

    private void generateSeats() {
        char[] rows = {'A', 'B', 'C', 'D', 'E', 'F'};
        int columns = 10;

        for (char row : rows) {
            for (int col = 1; col <= columns; col++) {
                final String seatId = row + String.valueOf(col);

                Button seatButton = new Button(this);
                seatButton.setText(seatId);
                seatButton.setTextSize(12);
                seatButton.setAllCaps(false);

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = 100;
                param.height = 100;
                param.setMargins(8, 8, 8, 8);
                seatButton.setLayoutParams(param);

                if (unavailableSeats.contains(seatId)) {
                    seatButton.setBackgroundColor(Color.parseColor("#FF4081")); // Unavailable
                    seatButton.setEnabled(false);
                } else {
                    seatButton.setBackgroundColor(Color.WHITE);
                    seatButton.setOnClickListener(v -> toggleSeat(seatId, seatButton));
                }

                seatGrid.addView(seatButton);
            }
        }

        updateSelectedSeatsInfo();
    }

    private void toggleSeat(String seatId, Button seatButton) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            seatButton.setBackgroundColor(Color.WHITE);
        } else {
            selectedSeats.add(seatId);
            seatButton.setBackgroundColor(Color.parseColor("#3F51B5")); // Selected
        }
        updateSelectedSeatsInfo();
    }

    private void updateSelectedSeatsInfo() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Selected Seats: None");
            tvTotalPrice.setText("Total: 0.00 LKR");
        } else {
            tvSelectedSeats.setText("Selected Seats: " + selectedSeats);
            int total = selectedSeats.size() * SEAT_PRICE;
            tvTotalPrice.setText("Total: " + total + ".00 LKR");
        }
    }

    private void saveBookedSeats() {
        DocumentReference docRef = db.collection("SeatSelection").document(docId);

        int totalPrice = selectedSeats.size() * SEAT_PRICE;
        String selectedTime = spinnerTime.getSelectedItem().toString();

        HashMap<String, Object> data = new HashMap<>();
        data.put("selectedSeats", new ArrayList<>(selectedSeats));
        data.put("movieTitle", movieTitle);
        data.put("username", username);
        data.put("date", selectedDate);
        data.put("time", selectedTime);
        data.put("totalPrice", totalPrice);

        docRef.set(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Seats booked successfully", Toast.LENGTH_SHORT).show();

                    // 🆕 Navigate to SubmissionFormActivity
                    Intent intent = new Intent(this, SubmissionFormActivity.class);
                    intent.putExtra("docId", docId);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to book seats", Toast.LENGTH_SHORT).show();
                });
    }

    private String getUsername() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("username", "defaultUser");
    }

    private String getMovieTitle() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getString("movieTitle", "Unknown Movie");
    }
}
