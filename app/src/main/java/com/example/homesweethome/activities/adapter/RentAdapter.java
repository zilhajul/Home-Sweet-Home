package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homesweethome.R;
import com.example.homesweethome.api.RetrofitClient;
import com.example.homesweethome.model.ApiResponse;
import com.example.homesweethome.model.Flat;
import com.example.homesweethome.model.Rent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentAdapter extends RecyclerView.Adapter<RentAdapter.RentViewHolder> {

    private List<Rent> flatList;
    private Context context;

    public RentAdapter(List<Rent> flatList, Context context) {
        this.flatList = flatList;
        this.context = context;
    }

    @NonNull
    @Override
    public RentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rent, parent, false);
        return new RentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RentViewHolder holder, int position) {
        Rent flat = flatList.get(position);

        // ১. ডাটা সেট করা (Null চেকসহ)
        holder.tvRentMonth.setText(flat.getRentMonth() != null ? flat.getRentMonth() : "N/A");
        holder.tvRentYear.setText(flat.getRentYear() != null ? String.valueOf(flat.getRentYear()) : "N/A");

        // ২. স্ট্যাটাস এবং কালার লজিক (সবচেয়ে গুরুত্বপূর্ণ অংশ)
        String status = (flat.getRentStatus() != null) ? flat.getRentStatus().trim() : "unpaid";
        holder.tvRentStatus.setText(status.toUpperCase());

        if (status.equalsIgnoreCase("paid")) {
            // পেইড হলে সবুজ কালার
            holder.tvRentStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_status_badge));
            holder.btnMpaid.setEnabled(false);
            holder.btnMpaid.setText("PAID");
            holder.btnMpaid.setAlpha(0.5f); // বাটনটি আবছা দেখাবে
        } else {
            // আনপেইড হলে লাল কালার এবং বাটন রিসেট
            holder.tvRentStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_button_outline_red));
            holder.btnMpaid.setEnabled(true);
            holder.btnMpaid.setText("Mark as Paid");
            holder.btnMpaid.setAlpha(1.0f); // বাটনটি উজ্জ্বল দেখাবে
            holder.btnMpaid.setVisibility(View.VISIBLE);
        }

        // বিলের তথ্য সেট করা
        holder.tvFlatRent.setText("৳ " + flat.getFlatRent());
        holder.tvGasBill.setText("৳ " + flat.getGasBill());
        holder.tvElectricityBill.setText("৳ " + flat.getElectricityBill());
        holder.tvWaterBill.setText("৳ " + flat.getWaterBill());
        holder.tvServiceCharge.setText("৳ " + flat.getServiceCharge());
        holder.tvTotal.setText("৳ " + flat.getTotalRent());

        // বাটন ক্লিক লজিক
        holder.btnMpaid.setOnClickListener(v -> {
            // ক্লিক করার সাথে সাথে বাটন ডিজেবল করুন যাতে বারবার রিকোয়েস্ট না যায়
            holder.btnMpaid.setEnabled(false);

            String paymentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            HashMap<String, String> body = new HashMap<>();
            body.put("_id", flat.getId());
            body.put("rent_status", "paid");
            body.put("payment_date", paymentDate);

            RetrofitClient.getInstance(context).getAuthService().updateRent(body)
                    .enqueue(new Callback<ApiResponse<Rent>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Rent>> call, Response<ApiResponse<Rent>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                // লোকাল ডাটা আপডেট
                                flat.setRentStatus("paid");
                                // নির্দিষ্ট আইটেম রিফ্রেশ (এটি কালার এবং টেক্সট সাথে সাথে চেঞ্জ করবে)
                                notifyItemChanged(holder.getAdapterPosition());
                                Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.btnMpaid.setEnabled(true); // ফেইল করলে বাটন আবার একটিভ করুন
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Rent>> call, Throwable t) {
                            holder.btnMpaid.setEnabled(true);
                            Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return flatList != null ? flatList.size() : 0;
    }

    public class RentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRentMonth, tvRentYear, tvRentStatus;
        private TextView tvFlatRent, tvGasBill, tvElectricityBill, tvWaterBill, tvServiceCharge, tvTotal , tvLivingF;
        private Button btnMpaid;
        public RentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize month, year, and status
            tvRentMonth = itemView.findViewById(R.id.tvRentMonth);
            tvRentYear = itemView.findViewById(R.id.tvRentYear);
            tvRentStatus = itemView.findViewById(R.id.tvRentStatus);

            // Initialize bill fields
            tvFlatRent = itemView.findViewById(R.id.tvFlatRent);
            tvGasBill = itemView.findViewById(R.id.tvGasBill);
            tvElectricityBill = itemView.findViewById(R.id.tvElectricityBill);
            tvWaterBill = itemView.findViewById(R.id.tvWaterBill);
            tvServiceCharge = itemView.findViewById(R.id.tvServiceCharge);
            tvTotal = itemView.findViewById(R.id.tvTotalRent);
            btnMpaid = itemView.findViewById(R.id.btnMarkPaid);
        }
    }

}