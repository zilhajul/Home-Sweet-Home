package com.example.homesweethome.activities.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.example.homesweethome.R;
import com.example.homesweethome.model.Subscription;

import java.util.List;
import java.util.Locale;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    // ─── Plan accent colors (cycles through for each plan) ──────────────────────
    private static final int[] PLAN_COLORS = {
            0xFF5B8DEF,   // Blue  – Free / basic
            0xFF7C5BEF,   // Purple – Mid tier
            0xFFEF8C5B,   // Orange – Premium
            0xFF5BEFB8,   // Teal  – Enterprise
    };

    private static final int[] HEADER_COLORS = {
            0xFF1E2540,
            0xFF221E40,
            0xFF3A2416,
            0xFF163A2E,
    };

    private final Context context;
    private final List<Subscription> subscriptionList;
    private final OnPurchaseClickListener purchaseClickListener;

    // ─── Interface ───────────────────────────────────────────────────────────────

    public interface OnPurchaseClickListener {
        void onPurchaseClick(Subscription subscription, int position);
    }

    // ─── Constructor ─────────────────────────────────────────────────────────────

    public SubscriptionAdapter(Context context,
                               List<Subscription> subscriptionList,
                               OnPurchaseClickListener listener) {
        this.context = context;
        this.subscriptionList = subscriptionList;
        this.purchaseClickListener = listener;
    }

    // ─── ViewHolder ──────────────────────────────────────────────────────────────

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        CardView cardRoot;
        View headerBanner;
        TextView tvPlanName;
        TextView tvStatus;
        TextView tvCurrencySymbol;
        TextView tvPrice;
        TextView tvDescription;
        TextView tvDuration;
        TextView tvMaxBuildings;
        TextView tvMaxFlats;
        MaterialButton btnPurchase;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot         = (CardView) itemView;
            headerBanner     = itemView.findViewById(R.id.headerBanner);
            tvPlanName       = itemView.findViewById(R.id.tvPlanName);
            tvStatus         = itemView.findViewById(R.id.tvStatus);
            tvCurrencySymbol = itemView.findViewById(R.id.tvCurrencySymbol);
            tvPrice          = itemView.findViewById(R.id.tvPrice);
            tvDescription    = itemView.findViewById(R.id.tvDescription);
            tvDuration       = itemView.findViewById(R.id.tvDuration);
            tvMaxBuildings   = itemView.findViewById(R.id.tvMaxBuildings);
            tvMaxFlats       = itemView.findViewById(R.id.tvMaxFlats);
            btnPurchase      = itemView.findViewById(R.id.btnPurchase);
        }
    }

    // ─── Adapter overrides ───────────────────────────────────────────────────────

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_subscription_card, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        Subscription plan = subscriptionList.get(position);

        // ── Accent color for this card ──
        int accentColor  = PLAN_COLORS[position % PLAN_COLORS.length];
        int headerColor  = HEADER_COLORS[position % HEADER_COLORS.length];

        // ── Plan name (title) ──
        holder.tvPlanName.setText(plan.getSubscriptionName());

        // ── Status badge ──
        String status = plan.getSubscriptionStatus();
        holder.tvStatus.setText(status.toUpperCase(Locale.getDefault()));
        if ("active".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundResource(R.drawable.badge_status_bg);
        }

        holder.headerBanner.setBackgroundColor(headerColor);

        double price = plan.getSubscriptionPrice();
        if (price == 0) {
            holder.tvCurrencySymbol.setVisibility(View.GONE);
            holder.tvPrice.setText("Free");
        } else {
            holder.tvCurrencySymbol.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(formatPrice(price));
        }

        // ── Description ──
        holder.tvDescription.setText(plan.getSubscriptionDescription());

        int duration = plan.getSubscriptionDuration();
        holder.tvDuration.setText(duration + (duration == 1 ? " Day" : " Days"));

        int buildings = plan.getBuildingAddMaxNumber();
        holder.tvMaxBuildings.setText(buildings + (buildings == 1 ? " Building" : " Buildings"));

        int flats = plan.getFlatAddMaxNumber();
        holder.tvMaxFlats.setText(flats + (flats == 1 ? " Flat" : " Flats"));

        holder.btnPurchase.setText(price == 0 ? "Get Started" : "Purchase Plan");
        holder.btnPurchase.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(accentColor)
        );
        holder.tvCurrencySymbol.setTextColor(accentColor);

        holder.btnPurchase.setOnClickListener(v -> {
            if (purchaseClickListener != null) {
                purchaseClickListener.onPurchaseClick(plan, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionList == null ? 0 : subscriptionList.size();
    }

    private String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((int) price);
        }
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    public void updateData(List<Subscription> newList) {
        subscriptionList.clear();
        subscriptionList.addAll(newList);
        notifyDataSetChanged();
    }
}
