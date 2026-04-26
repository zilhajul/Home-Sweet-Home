package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homesweethome.R;
import com.example.homesweethome.model.Flat;

import java.util.List;

public class FlatAdapter extends RecyclerView.Adapter<FlatAdapter.FlatViewHolder> {

    private List<Flat> flatList;
    private Context context;

    public FlatAdapter(List<Flat> flatList, Context context) {
        this.flatList = flatList;
        this.context = context;
    }

    @NonNull
    @Override
    public FlatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flat, parent, false);
        return new FlatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlatViewHolder holder, int position) {
        Flat flat = flatList.get(position);
        android.util.Log.d("FlatAdapter", "Displaying Flat: " + flat.getFlatName());

        holder.tvFlatName.setText(String.valueOf(flat.getFlatName()));
        holder.tvFloorNumber.setText(String.valueOf(flat.getFloorNumber()));
        holder.tvFlatRent.setText(flat.getFlatRent() + " BDT");
        holder.tvStatus.setText(flat.getFlatStatus());
        holder.tvWaterBill.setText(flat.getWaterBill() + " BDT");
        holder.tvGasBill.setText(flat.getGasBill() + " BDT");
        holder.tvElectricityBill.setText(flat.getElectricityBill() + " BDT");
        holder.tvServiceCharge.setText(flat.getServiceCharge() + " BDT");


        Glide.with(context)
                .load(flat.getFlatImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivFlatImage);
    }

    @Override
    public int getItemCount() {
        return flatList != null ? flatList.size() : 0;
    }

    public static class FlatViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFlatImage;
        TextView tvFlatName, tvFloorNumber, tvFlatRent, tvStatus, tvGasBill, tvElectricityBill, tvWaterBill, tvServiceCharge;

        public FlatViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFlatImage = itemView.findViewById(R.id.ivFlatImage);
            tvFlatName = itemView.findViewById(R.id.tvFlatName);
            tvFloorNumber = itemView.findViewById(R.id.tvFloorNumber);
            tvFlatRent = itemView.findViewById(R.id.tvFlatRent);
            tvStatus = itemView.findViewById(R.id.tvFlatStatus);
            tvGasBill = itemView.findViewById(R.id.tvGasBill);
            tvElectricityBill = itemView.findViewById(R.id.tvElectricityBill);
            tvWaterBill = itemView.findViewById(R.id.tvWaterBill);
            tvServiceCharge = itemView.findViewById(R.id.tvServiceCharge);
        }
    }
}
