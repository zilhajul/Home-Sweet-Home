package com.example.homesweethome.activities.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.homesweethome.R;
import com.example.homesweethome.activities.adapter.FlatAdapter;
import com.example.homesweethome.api.RetrofitClient;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buildingId = getArguments().getString("building_id");
            landlordId = getArguments().getString("landlord_id");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flat_list, container, false);


        rvFlats = view.findViewById(R.id.rvFlats);
        rvFlats.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FlatAdapter(flatList, requireContext());
        rvFlats.setAdapter(adapter);

        sessionManager = new SessionManager(requireContext());


        fetchFlats();

        return view;
    }

    private void fetchFlats() {

        RetrofitClient client = RetrofitClient.getInstance(requireContext());
        client.setSessionManager(sessionManager);

                client.getFlatService()
                .getFlatsByBuilding(landlordId, buildingId)
                .enqueue(new Callback<FlatResponse>() {


                    @Override
                    public void onResponse(Call<FlatResponse> call, Response<FlatResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Flat> flatsFromApi = response.body().getData();

                            if (flatsFromApi != null && !flatsFromApi.isEmpty()) {
                                flatList.clear();
                                flatList.addAll(flatsFromApi);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FlatResponse> call, Throwable throwable) {

                    }
                });
    }
}