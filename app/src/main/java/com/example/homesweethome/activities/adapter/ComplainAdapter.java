package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Complain;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder> {

    private List<Complain> complainList;
    private Context context;


    public ComplainAdapter(List<Complain> complainList, Context context) {

        this.complainList = complainList;
        this.context = context;

    }

    @NonNull
    @Override
    public ComplainAdapter.ComplainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complain, parent, false);
        return new ComplainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainAdapter.ComplainViewHolder holder, int position) {

        Complain complain = complainList.get(position);
        holder.tvTenantName.setText(complain.getTenant_id().getTenantName());
        holder.tvFlatInfo.setText(complain.getFlat_id().getFlatName()+" | "+complain.getBuilding_id().getBuildingName());
        holder.tvComplainText.setText(complain.getComplain_text());
        holder.tvStatus.setText(complain.getComplain_status());
        holder.tvResponse.setText(complain.getComplain_response());
        holder.tvDate.setText(complain.getCreatedAt());

        String status= complain.getComplain_status();


        if (status.equals("pending")){
            holder.etResponse.setVisibility(View.VISIBLE);
            holder.btnCancle.setVisibility(View.VISIBLE);
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(context.getColor(R.color.colorStatusPartial));
        }else if(status.equals("Resolved") || status.equals("Rejected")) {
            holder.etResponse.setVisibility(View.GONE);
            holder.btnCancle.setVisibility(View.GONE);
            holder.btnConfirm.setVisibility(View.GONE);
            if (status.equals("Resolved")){
                holder.tvStatus.setTextColor(context.getColor(R.color.colorStatusPaid));
            }else {
                holder.tvStatus.setTextColor(context.getColor(R.color.colorStatusUnpaid));
            }
        }

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String response = holder.etResponse.getText().toString();

                HashMap<String, String> body = new HashMap<>();
                body.put("complain_response", response);
                body.put("complain_status", "Resolved");
                body.put("_id", complain.get_id());

                RetrofitClient.getInstance(context).getAuthService().updateComplain(body)
                        .enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse apiResponse = response.body();
                                    if (apiResponse.isSuccess()) {
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Response Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable throwable) {

                            }
                        });




            }
        });

        holder.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = holder.etResponse.getText().toString();

                HashMap<String, String> body = new HashMap<>();
                body.put("complain_response", response);
                body.put("complain_status", "Rejected");
                body.put("_id", complain.get_id());

                RetrofitClient.getInstance(context).getAuthService().updateComplain(body)
                        .enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse apiResponse = response.body();
                                    if (apiResponse.isSuccess()) {
                                        notifyItemChanged(position);
                                        Toast.makeText(context, "Response Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable throwable) {

                            }
                        });




            }
        });



    }

    @Override
    public int getItemCount() {
        return complainList.size();
    }

    public class ComplainViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTenantName, tvFlatInfo, tvComplainText, tvStatus, tvResponse, tvDate;
        private EditText etResponse;
        private Button btnCancle, btnConfirm;

        public ComplainViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenantName = itemView.findViewById(R.id.tvTenantName);
            tvFlatInfo = itemView.findViewById(R.id.tvFlatInfo);
            tvComplainText = itemView.findViewById(R.id.tvComplainText);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            tvDate = itemView.findViewById(R.id.tvDate);
            etResponse = itemView.findViewById(R.id.etResponse);
            btnCancle = itemView.findViewById(R.id.btnCancle);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);

        }
    }
}
