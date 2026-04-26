package com.example.homesweethome.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.LandlordAddBuildingActivity;
import com.example.homesweethome.activities.adapter.BuildingAdapter;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Building;
import com.example.homesweethome.preferences.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalProperties extends Fragment implements BuildingAdapter.OnBuildingClickListener {

    private RecyclerView rvBuildings;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private LinearLayout errorState;
    private TextView tvBuildingCount;
    private TextView tvErrorMessage;
    private Button btnAddFirst;
    private Button btnRetry;
    private View btnBack;

    private BuildingAdapter adapter;
    private SessionManager sessionManager;
    private String landlordId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_total_properties, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvBuildings = view.findViewById(R.id.rvBuildings);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);
        tvBuildingCount = view.findViewById(R.id.tvBuildingCount);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);
        btnAddFirst = view.findViewById(R.id.btnAddFirst);
        btnRetry = view.findViewById(R.id.btnRetry);
        btnBack = view.findViewById(R.id.ivBack);

        // Initialize SessionManager
        sessionManager = new SessionManager(requireContext());
        landlordId = sessionManager.getLandlord().getId();

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvBuildings.setLayoutManager(layoutManager);
        adapter = new BuildingAdapter(null, requireContext(), this);
        rvBuildings.setAdapter(adapter);

        // Set click listeners
        btnAddFirst.setOnClickListener(v -> openAddBuildingActivity());
        btnRetry.setOnClickListener(v -> fetchBuildings());
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Fetch buildings
        fetchBuildings();
    }

    private void fetchBuildings() {

        if (landlordId == null) {
            showErrorState("User session expired. Please login again.");
            return;
        }

        showLoading();


        Context context = getContext();
        if (context == null) return;

        RetrofitClient client = RetrofitClient.getInstance(context);
        client.setSessionManager(sessionManager);

                client.getBuildingService()
                .getBuildingsByLandlord(landlordId)
                .enqueue(new Callback<ApiResponse<List<Building>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Building>>> call,
                                           Response<ApiResponse<List<Building>>> response) {

                        if (!isAdded() || getContext() == null) return;

                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Building>> apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                List<Building> buildings = apiResponse.getData();
                                if (buildings != null && !buildings.isEmpty()) {
                                    displayBuildings(buildings);
                                } else {
                                    showEmptyState();
                                }
                            } else {
                                showErrorState(apiResponse.getMessage());
                            }
                        } else {
                            showErrorState("Server error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Building>>> call, Throwable t) {
                        if (!isAdded()) return;
                        hideLoading();
                        showErrorState("Connection failed. Please check your internet.");
                    }
                });
    }

    private void displayBuildings(List<Building> buildings) {
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        rvBuildings.setVisibility(View.VISIBLE);


        tvBuildingCount.setText(buildings.size() + " Properties");


        adapter.updateList(buildings);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvBuildings.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
        rvBuildings.setVisibility(View.GONE);
        tvBuildingCount.setText("0 Properties");
    }

    private void showErrorState(String message) {
        errorState.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        rvBuildings.setVisibility(View.GONE);
        tvErrorMessage.setText(message);
    }

    private void openAddBuildingActivity() {
        Intent intent = new Intent(requireContext(), LandlordAddBuildingActivity.class);
        intent.putExtra("landlordId", landlordId);
        startActivity(intent);
    }

    @Override
    public void onViewClick(Building building) {
        Fragment flatFragment = new FlatListFragment();

        Bundle args = new Bundle();
        args.putString("building_id", building.getId());
        args.putString("landlord_id", landlordId);
        flatFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, flatFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditClick(Building building) {
        Toast.makeText(requireContext(), "Edit: " + building.getBuildingName(), Toast.LENGTH_SHORT).show();
    }
}