package com.example.homesweethome.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.example.homesweethome.R;
import com.example.homesweethome.activities.adapter.SubscriptionAdapter;
import com.example.homesweethome.api.AuthService;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Subscription;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity
        implements SubscriptionAdapter.OnPurchaseClickListener {

    private RecyclerView recyclerSubscriptions;
    private View loadingOverlay;
    private MaterialToolbar toolbar;

    private SubscriptionAdapter adapter;
    private final List<Subscription> subscriptionList = new ArrayList<>();

    private AuthService authService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        initViews();
        setupToolbar();
        setupRecyclerView();
        initAuthService();

        fetchSubscriptions();
    }


    private void initViews() {
        toolbar                = findViewById(R.id.toolbar);
        recyclerSubscriptions  = findViewById(R.id.recyclerSubscriptions);
        loadingOverlay         = findViewById(R.id.loadingOverlay);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new SubscriptionAdapter(this, subscriptionList, this);
        recyclerSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSubscriptions.setAdapter(adapter);

        // Smooth item animations
        recyclerSubscriptions.setHasFixedSize(false);
    }

    private void initAuthService() {
        authService = RetrofitClient.getInstance(this).getAuthService();
    }

    private void fetchSubscriptions() {
        showLoading(true);

        Call<ApiResponse<List<Subscription>>> call = authService.getSubscriptions();
        call.enqueue(new Callback<ApiResponse<List<Subscription>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Subscription>>> call,
                                   Response<ApiResponse<List<Subscription>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Subscription>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Subscription> subscriptions = apiResponse.getData();
                        if (!subscriptions.isEmpty()) {
                            adapter.updateData(subscriptions);
                            return;
                        }
                    }
                }

//                loadSampleData();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Subscription>>> call, Throwable t) {
                showLoading(false);
                // Network failed — load sample data so the UI is still usable
//                loadSampleData();
                Toast.makeText(
                        SubscriptionActivity.this,
                        "Could not reach server. Showing sample plans.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadSampleData() {
        List<Subscription> samples = new ArrayList<>();

        Subscription free = new Subscription(
                "69c3d2f0026e52df0f6642d0",
                "Free",
                "active",
                "Get started at no cost. Perfect for trying out our platform.",
                0,
                7,
                1,
                10,
                "2026-03-25T12:20:00.308Z",
                "2026-03-25T12:20:00.308Z"
        );

        Subscription basic = new Subscription(
                "69c3d2f0026e52df0f6642d1",
                "Basic",
                "active",
                "A solid plan for small property managers getting started.",
                9.99,
                30,
                3,
                50,
                "2026-03-25T12:20:00.308Z",
                "2026-03-25T12:20:00.308Z"
        );

        Subscription pro = new Subscription(
                "69c3d2f0026e52df0f6642d2",
                "Pro",
                "active",
                "Ideal for growing portfolios that need more power and flexibility.",
                29.99,
                30,
                10,
                200,
                "2026-03-25T12:20:00.308Z",
                "2026-03-25T12:20:00.308Z"
        );

        Subscription enterprise = new Subscription(
                "69c3d2f0026e52df0f6642d3",
                "Enterprise",
                "active",
                "Unlimited scale for large property management companies.",
                99.99,
                30,
                100,
                9999,
                "2026-03-25T12:20:00.308Z",
                "2026-03-25T12:20:00.308Z"
        );

        samples.add(free);
        samples.add(basic);
        samples.add(pro);
        samples.add(enterprise);

        adapter.updateData(samples);
    }


    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onPurchaseClick(Subscription subscription, int position) {
        if (subscription.getSubscriptionPrice() == 0) {
            // Free plan — activate directly
            Toast.makeText(
                    this,
                    subscription.getSubscriptionName() + " plan activated!",
                    Toast.LENGTH_SHORT
            ).show();
            // TODO: call your API to activate the free plan for the current user
        } else {
            // Paid plan — start payment flow
            Toast.makeText(
                    this,
                    "Starting purchase for " + subscription.getSubscriptionName() + " plan…",
                    Toast.LENGTH_SHORT
            ).show();

            // TODO: Launch PaymentActivity or Google Play billing
            // Example:
            // Intent intent = new Intent(this, PaymentActivity.class);
            // intent.putExtra("subscription_id", subscription.getId());
            // intent.putExtra("subscription_price", subscription.getSubscriptionPrice());
            // startActivity(intent);
        }
    }
}
