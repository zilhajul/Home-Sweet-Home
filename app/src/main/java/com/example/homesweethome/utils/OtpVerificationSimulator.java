package com.example.homesweethome.utils;

/**
 * OTP Verification Simulator for Testing
 * এই ক্লাসটি সার্ভার সাইড OTP ভেরিফিকেশন লজিক সিমুলেট করে
 */
public class OtpVerificationSimulator {
    
    private static String storedOtp = null;
    private static String storedPhone = null;
    private static long otpCreatedTime = 0;
    private static final long OTP_EXPIRY_TIME_MS = 5 * 60 * 1000; // 5 minutes

    /**
     * সার্ভারে OTP সংরক্ষণ সিমুলেট করুন
     */
    public static void storeOtp(String phone, String otp) {
        storedPhone = phone;
        storedOtp = otp;
        otpCreatedTime = System.currentTimeMillis();
        
        android.util.Log.d("OtpSimulator", "📦 OTP Stored in Simulator");
        android.util.Log.d("OtpSimulator", "  Phone: " + storedPhone);
        android.util.Log.d("OtpSimulator", "  OTP: " + storedOtp);
        android.util.Log.d("OtpSimulator", "  Type: " + (storedOtp != null ? storedOtp.getClass().getSimpleName() : "null"));
        android.util.Log.d("OtpSimulator", "  Length: " + (storedOtp != null ? storedOtp.length() : 0));
        android.util.Log.d("OtpSimulator", "  Created At: " + otpCreatedTime);
    }

    /**
     * সার্ভারে OTP ভেরিফিকেশন সিমুলেট করুন
     */
    public static boolean verifyOtp(String phone, String otp) {
        android.util.Log.d("OtpSimulator", "🔍 Verifying OTP in Simulator");
        android.util.Log.d("OtpSimulator", "--- Received from Client ---");
        android.util.Log.d("OtpSimulator", "  Phone: '" + phone + "' (Type: " + (phone != null ? phone.getClass().getSimpleName() : "null") + ")");
        android.util.Log.d("OtpSimulator", "  OTP: '" + otp + "' (Type: " + (otp != null ? otp.getClass().getSimpleName() : "null") + ")");
        
        android.util.Log.d("OtpSimulator", "--- Stored in Simulator ---");
        android.util.Log.d("OtpSimulator", "  Phone: '" + storedPhone + "'");
        android.util.Log.d("OtpSimulator", "  OTP: '" + storedOtp + "'");
        
        // Check if OTP exists
        if (storedOtp == null) {
            android.util.Log.e("OtpSimulator", "❌ No OTP found in simulator");
            return false;
        }
        
        // Check expiration
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - otpCreatedTime;
        
        android.util.Log.d("OtpSimulator", "--- Expiration Check ---");
        android.util.Log.d("OtpSimulator", "  Created At: " + otpCreatedTime);
        android.util.Log.d("OtpSimulator", "  Current Time: " + currentTime);
        android.util.Log.d("OtpSimulator", "  Elapsed: " + (elapsedTime / 1000) + " seconds");
        android.util.Log.d("OtpSimulator", "  Expiry Time: " + (OTP_EXPIRY_TIME_MS / 1000) + " seconds");
        
        if (elapsedTime > OTP_EXPIRY_TIME_MS) {
            android.util.Log.e("OtpSimulator", "❌ OTP Expired!");
            return false;
        }
        
        // Normalize inputs
        String normalizedPhone = phone != null ? phone.trim() : "";
        String normalizedOtp = otp != null ? otp.trim() : "";
        String normalizedStoredPhone = storedPhone != null ? storedPhone.trim() : "";
        String normalizedStoredOtp = storedOtp != null ? storedOtp.trim() : "";
        
        android.util.Log.d("OtpSimulator", "--- Normalized Values ---");
        android.util.Log.d("OtpSimulator", "  Client Phone (normalized): '" + normalizedPhone + "'");
        android.util.Log.d("OtpSimulator", "  Stored Phone (normalized): '" + normalizedStoredPhone + "'");
        android.util.Log.d("OtpSimulator", "  Client OTP (normalized): '" + normalizedOtp + "'");
        android.util.Log.d("OtpSimulator", "  Stored OTP (normalized): '" + normalizedStoredOtp + "'");
        
        // Check phone match
        boolean phoneMatch = normalizedPhone.equals(normalizedStoredPhone);
        android.util.Log.d("OtpSimulator", "  Phone Match: " + phoneMatch);
        
        // Check OTP match
        boolean otpMatch = normalizedOtp.equals(normalizedStoredOtp);
        android.util.Log.d("OtpSimulator", "  OTP Match: " + otpMatch);
        
        // Check type match
        boolean typeMatch = (otp instanceof String) && (storedOtp instanceof String);
        android.util.Log.d("OtpSimulator", "  Type Match: " + typeMatch);
        
        // Final result
        boolean result = phoneMatch && otpMatch && typeMatch;
        
        if (result) {
            android.util.Log.d("OtpSimulator", "✅ OTP Verification SUCCESSFUL");
        } else {
            android.util.Log.e("OtpSimulator", "❌ OTP Verification FAILED");
            android.util.Log.e("OtpSimulator", "  Reasons:");
            if (!phoneMatch) android.util.Log.e("OtpSimulator", "    - Phone number doesn't match");
            if (!otpMatch) android.util.Log.e("OtpSimulator", "    - OTP doesn't match");
            if (!typeMatch) android.util.Log.e("OtpSimulator", "    - Type mismatch");
        }
        
        return result;
    }

    /**
     * সিমুলেটর রিসেট করুন
     */
    public static void reset() {
        storedOtp = null;
        storedPhone = null;
        otpCreatedTime = 0;
        android.util.Log.d("OtpSimulator", "🔄 Simulator Reset");
    }

    /**
     * সিমুলেটর স্ট্যাটাস দেখুন
     */
    public static void printStatus() {
        android.util.Log.d("OtpSimulator", "=== Simulator Status ===");
        android.util.Log.d("OtpSimulator", "Stored Phone: " + storedPhone);
        android.util.Log.d("OtpSimulator", "Stored OTP: " + storedOtp);
        android.util.Log.d("OtpSimulator", "Created Time: " + otpCreatedTime);
        
        if (storedOtp != null) {
            long elapsedTime = System.currentTimeMillis() - otpCreatedTime;
            long remainingTime = OTP_EXPIRY_TIME_MS - elapsedTime;
            android.util.Log.d("OtpSimulator", "Remaining Time: " + (remainingTime / 1000) + " seconds");
        }
    }
}

