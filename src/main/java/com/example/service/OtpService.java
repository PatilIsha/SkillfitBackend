package com.example.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class OtpService {
    
    // Store OTPs temporarily (email -> OTP, expiry time)
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds
    private final Random random = new Random();
    
    // Inner class to store OTP and expiry time
    private static class OtpData {
        String otp;
        long expiryTime;
        String name;
        String password; // For recruiters
        
        OtpData(String otp, String name) {
            this.otp = otp;
            this.expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME;
            this.name = name;
        }
        
        OtpData(String otp, String name, String password) {
            this.otp = otp;
            this.expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME;
            this.name = name;
            this.password = password;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    /**
     * Generate and store OTP for email verification
     * @param email Email address
     * @param name User's name
     * @return Generated OTP
     */
    public String generateOtp(String email, String name) {
        String otp = generateRandomOtp();
        otpStore.put(email, new OtpData(otp, name));
        return otp;
    }
    
    /**
     * Generate and store OTP for recruiter (with password)
     * @param email Email address
     * @param name User's name
     * @param password User's password
     * @return Generated OTP
     */
    public String generateOtp(String email, String name, String password) {
        String otp = generateRandomOtp();
        otpStore.put(email, new OtpData(otp, name, password));
        return otp;
    }
    
    /**
     * Verify OTP for email
     * @param email Email address
     * @param otp OTP to verify
     * @return true if valid, false otherwise
     */
    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStore.get(email);
        if (data == null) {
            return false;
        }
        if (data.isExpired()) {
            otpStore.remove(email);
            return false;
        }
        if (data.otp.equals(otp)) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }
    
    /**
     * Get user data after OTP verification (removes from store)
     * @param email Email address
     * @return OtpData or null if not found/expired
     */
    public OtpData getUserData(String email) {
        OtpData data = otpStore.get(email);
        if (data != null && !data.isExpired()) {
            return data;
        }
        if (data != null) {
            otpStore.remove(email);
        }
        return null;
    }
    
    /**
     * Generate random 6-digit OTP
     */
    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    /**
     * Get stored name for email (without removing)
     */
    public String getName(String email) {
        OtpData data = otpStore.get(email);
        if (data == null || data.isExpired()) {
            if (data != null) {
                otpStore.remove(email);
            }
            return null;
        }
        return data.name;
    }
    
    /**
     * Get stored password for email (for recruiters)
     */
    public String getPassword(String email) {
        OtpData data = otpStore.get(email);
        if (data == null || data.isExpired()) {
            if (data != null) {
                otpStore.remove(email);
            }
            return null;
        }
        return data.password;
    }
}
