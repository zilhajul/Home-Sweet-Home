package com.example.homesweethome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homesweethome.activities.LandlordDashboardActivity;
import com.example.homesweethome.activities.SubscriptionActivity;

public class PaymentWebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_webview);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // Set custom WebViewClient to handle deep linking
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);

                // Check if it's a custom deep link
                if (uri.getScheme() != null && uri.getScheme().equals("myapp")) {
                    handleDeepLink(uri);
                    return true;
                }

                // For other URLs, load normally
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Uri uri = Uri.parse(url);

                // Check if it's a custom deep link
                if (uri.getScheme() != null && uri.getScheme().equals("myapp")) {
                    handleDeepLink(uri);
                    return true;
                }

                // For other URLs, load normally
                return false;
            }
        });

        // Get URL from Intent
        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            webView.loadUrl(paymentUrl);
        } else {
            // Fallback to dummy URL if no URL provided
//            webView.loadUrl("https://houserent.ilhamcollection.com/login");
            Toast.makeText(this, "No url found", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeepLink(Uri uri) {
        if (uri.getHost() != null && uri.getHost().equals("open")) {
            String payment_status = uri.getQueryParameter("payment_status");
            String invoice_id = uri.getQueryParameter("invoice_id");

            if (payment_status.trim().toLowerCase().equals("success")){
                Intent intent = new Intent(this, LandlordDashboardActivity.class);
                intent.putExtra("payment_status", payment_status);
                intent.putExtra("invoice_id", invoice_id);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(this, SubscriptionActivity.class);
                intent.putExtra("isNeed", true);
                startActivity(intent);
                finish();
            }
        }
    }

}