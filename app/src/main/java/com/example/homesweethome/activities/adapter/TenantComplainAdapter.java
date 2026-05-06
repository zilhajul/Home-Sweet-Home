package com.example.homesweethome.activities.adapter;

import android.content.Context;
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

public class TenantComplainAdapter extends RecyclerView.Adapter<TenantComplainAdapter.ComplainViewHolder> {

    private List<Complain> complainList;
    private Context context;


    public TenantComplainAdapter(List<Complain> complainList, Context context) {

        this.complainList = complainList;
        this.context = context;

    }

    @NonNull
    @Override
    public TenantComplainAdapter.ComplainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complain_tenant, parent, false);
        return new ComplainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TenantComplainAdapter.ComplainViewHolder holder, int position) {

        Complain complain = complainList.get(position);
        holder.tvTenantName.setText(complain.getTenant_id().getTenantName());
        holder.tvFlatInfo.setText(complain.getFlat_id().getFlatName()+" | "+complain.getBuilding_id().getBuildingName());
        holder.tvComplainText.setText(complain.getComplain_text());
        holder.tvStatus.setText(complain.getComplain_status());
        holder.tvResponse.setText(complain.getComplain_response());
        holder.tvDate.setText(complain.getCreatedAt());

        String status= complain.getComplain_status();


        if (status.equals("Resolved")){
            holder.tvStatus.setTextColor(context.getColor(R.color.colorStatusPaid));
        }else {
            holder.tvStatus.setTextColor(context.getColor(R.color.colorStatusUnpaid));
        }



    }

    @Override
    public int getItemCount() {
        return complainList.size();
    }

    public static class ComplainViewHolder extends RecyclerView.ViewHolder {

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
        }
    }
}
