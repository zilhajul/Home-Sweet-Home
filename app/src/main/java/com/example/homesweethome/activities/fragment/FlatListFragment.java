package com.example.homesweethome.activities.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.adapter.FlatAdapter;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.FlatResponse;
import com.example.homesweethome.preferences.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FlatListFragment extends Fragment {
    private String buildingId, landlordId;
    private SessionManager sessionManager;
    private RecyclerView rvFlats;
    private FlatAdapter adapter;
    private List<Flat> flatList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView nothing_found_txt, tvFlatCount;
    private ImageView btnBack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString("building_id");
            landlordId = getArguments().getString("landlord_id");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFlats();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flat_list, container, false);


        rvFlats = view.findViewById(R.id.rvFlats);
        progressBar = view.findViewById(R.id.progressBar);
        nothing_found_txt = view.findViewById(R.id.nothing_found_txt);
        rvFlats.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FlatAdapter(flatList, requireContext(), buildingId, landlordId);
        rvFlats.setAdapter(adapter);



        btnBack = view.findViewById(R.id.ivBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());


        sessionManager = new SessionManager(requireContext());


        fetchFlats();

        tvFlatCount = view.findViewById(R.id.tvFlatCount);

        return view;
    }

    private void fetchFlats() {


        progressBar.setVisibility(VISIBLE);
        RetrofitClient client = RetrofitClient.getInstance(requireContext());
        client.setSessionManager(sessionManager);

                client.getFlatService()
                .getFlatsByBuilding(landlordId, buildingId)
                .enqueue(new Callback<FlatResponse>() {


                    @Override
                    public void onResponse(Call<FlatResponse> call, Response<FlatResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Flat> flatsFromApi = response.body().getData();

                            progressBar.setVisibility(GONE);

                            if (flatsFromApi != null && !flatsFromApi.isEmpty()) {
                                flatList.clear();
                                flatList.addAll(flatsFromApi);
                                adapter.notifyDataSetChanged();

                                tvFlatCount.setText(flatList.size() + " Flats");

                            }else {
                                rvFlats.setVisibility(GONE);
                                nothing_found_txt.setVisibility(VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FlatResponse> call, Throwable throwable) {
                        rvFlats.setVisibility(GONE);
                        nothing_found_txt.setVisibility(VISIBLE);
                        nothing_found_txt.setText(throwable.getMessage());
                        Toast.makeText(getContext(), "Flat fetch failed due to "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("FlatListFragment", "Flat fetch failed due to: " + throwable.getMessage());
                        progressBar.setVisibility(GONE);
                    }
                });
    }
}