package com.example.homesweethome.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.preferences.SessionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Generate_Rent extends AppCompatActivity {

    private String landlordId;
    private Spinner spinnerMonth, spinnerYear;
    private TextView tvSelectedDate;
    private Button  btnConfirm;
    private SessionManager sessionManager;

    List<String> yearList = new ArrayList<>();
    int currentYear, currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_rent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        landlordId = getIntent().getStringExtra("landlordId");

        spinnerMonth   = findViewById(R.id.spinnerMonth);
        spinnerYear    = findViewById(R.id.spinnerYear);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnConfirm     = findViewById(R.id.btnConfirm);

        // Get current date info
        Calendar calendar = Calendar.getInstance();
        currentYear  = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH); // 0-based (0 = January)

        setupMonthSpinner();
        setupYearSpinner();
        setupButtons();


    }

    private void setupMonthSpinner() {
        // Months are defined in res/values/arrays.xml as months_array
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.months_array,
                android.R.layout.simple_spinner_item
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Pre-select current month
        spinnerMonth.setSelection(currentMonth);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedDateLabel();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupYearSpinner() {
        // Generate year range: 20 years back to 10 years forward
        for (int y = currentYear - 20; y <= currentYear + 10; y++) {
            yearList.add(String.valueOf(y));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                yearList
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Pre-select current year
        int currentYearIndex = yearList.indexOf(String.valueOf(currentYear));
        spinnerYear.setSelection(currentYearIndex);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedDateLabel();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupButtons() {

        // Confirm button — get selected values
        btnConfirm.setOnClickListener(v -> {
            String selectedMonth = spinnerMonth.getSelectedItem().toString();
            String selectedYear  = spinnerYear.getSelectedItem().toString();
            Toast.makeText(this, "Selected: " + selectedMonth + " " + selectedYear, Toast.LENGTH_SHORT).show();


            HashMap<String, String> body = new HashMap<>();

            body.put("landlord_id", landlordId);
            body.put("rent_month", selectedMonth);
            body.put("rent_year", selectedYear);

           RetrofitClient client= RetrofitClient.getInstance(this);
           client.setSessionManager(sessionManager);
           client.getAuthService().addRent(body)
                    .enqueue(new Callback<ApiResponse>() {

                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(Generate_Rent.this, "Rent generated successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Generate_Rent.this, "Failed to generate rent. Please try again.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable throwable) {

                            Toast.makeText(Generate_Rent.this, "Failed to generate rent. Please try again.", Toast.LENGTH_SHORT).show();

                        }
                    });


        });
    }

    private void updateSelectedDateLabel() {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedYear  = spinnerYear.getSelectedItem().toString();
        tvSelectedDate.setText(selectedMonth + " " + selectedYear);
    }
}