package com.example.homesweethome.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.adapter.ComplainAdapter;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Complain;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification extends AppCompatActivity {

    private String landlordId;
    private RecyclerView rvNotification;
    private TextView tvNoNotifications;
    private List<Complain> complainList = new ArrayList<>();
    private  ComplainAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        landlordId = getIntent().getStringExtra("landlordId");
        rvNotification = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);

        getComplains();

        adapter = new ComplainAdapter(complainList,getApplicationContext());
        rvNotification.setLayoutManager(new LinearLayoutManager(this));
        rvNotification.setAdapter(adapter);


    }

    public void getComplains(){
        RetrofitClient.getInstance(this).getAuthService().getComplains(landlordId,null)
                .enqueue(new Callback<ApiResponse<List<Complain>>>() {


                    @Override
                    public void onResponse(Call<ApiResponse<List<Complain>>> call, Response<ApiResponse<List<Complain>>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Complain>> apiResponse = response.body();

                            if (apiResponse.isSuccess() && apiResponse.getData() != null ){

                                List<Complain> apiComplains = apiResponse.getData();

                                complainList.clear();
                                complainList.addAll(apiComplains);
                                adapter.notifyDataSetChanged();

                                tvNoNotifications.setVisibility(View.GONE);
                                rvNotification.setVisibility(View.VISIBLE);

                                Toast.makeText(Notification.this, "Notification Fetched Successfully", Toast.LENGTH_SHORT).show();


                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Complain>>> call, Throwable throwable) {

                    }
                });
    }
}