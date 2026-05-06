package com.example.homesweethome.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import com.example.homesweethome.activities.adapter.TenantAdapter;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.Tenant;
import com.example.homesweethome.model.TenantResponse;
import com.example.homesweethome.preferences.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowAllTenants extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView nothing_found_txt;
    private RecyclerView rvTenants;
    private SearchView searchView;


    private List<Tenant> tenantList = new ArrayList<>();
    private TenantAdapter adapter;
    private String flatId;
    private String buildingId,landlordId;
    private String action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_all_tenants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        flatId = getIntent().getStringExtra("flatId");
        buildingId = getIntent().getStringExtra("buildingId");
        landlordId = getIntent().getStringExtra("landlordId");
        action = getIntent().getStringExtra("action");





        searchView = findViewById(R.id.searchView);

        searchView.setIconified(false);
        searchView.clearFocus();

        progressBar = findViewById(R.id.progressBar1);
        nothing_found_txt = findViewById(R.id.tvNoTenants);
        rvTenants = findViewById(R.id.rvTenants);
        rvTenants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TenantAdapter(tenantList, this, flatId, buildingId, landlordId, action);
        rvTenants.setAdapter(adapter);



        FetchAllTenants();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.filter(newText);
                return true;
            }
        });
    }

    private void FetchAllTenants() {

        RetrofitClient client = RetrofitClient.getInstance(this);

        client.setSessionManager(new SessionManager(this));
        client.getAuthService()
                .getAllTenants()
                .enqueue(new Callback<TenantResponse>() {
                    @Override
                    public void onResponse(Call<TenantResponse> call, Response<TenantResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            List<Tenant> tenantFromApi = response.body().getData();
                            progressBar.setVisibility(View.GONE);

                            if (tenantFromApi != null && !tenantFromApi.isEmpty()) {
                                tenantList.clear();
                                tenantList.addAll(tenantFromApi);
                                adapter.notifyDataSetChanged();


                            } else {
                                nothing_found_txt.setVisibility(View.VISIBLE);
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<TenantResponse> call, Throwable throwable) {

                        progressBar.setVisibility(View.GONE);
                        nothing_found_txt.setVisibility(View.VISIBLE);

                        Toast.makeText(ShowAllTenants.this, "Tenant fetch failed due to "+throwable.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }
}