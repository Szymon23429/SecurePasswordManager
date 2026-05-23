package com.example.securepasswordmanager.security;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurityManager {
    private static SecurityManager instance;
    private SecretKeySpec secretKey;
    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_PIN = "master_pin";

    private SecurityManager() {}

    public static synchronized SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    public void setSecretKey(SecretKeySpec key) {
        this.secretKey = key;
    }

    public SecretKeySpec getSecretKey() {
        return secretKey;
    }
    
    public boolean isLocked() {
        return secretKey == null;
    }

    public void lock() {
        secretKey = null;
    }

    public void savePinForBiometrics(Context context, String pin) {
        try {
            SharedPreferences sharedPreferences = getEncryptedPrefs(context);
            sharedPreferences.edit().putString(KEY_PIN, pin).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStoredPin(Context context) {
        try {
            SharedPreferences sharedPreferences = getEncryptedPrefs(context);
            return sharedPreferences.getString(KEY_PIN, null);
        } catch (Exception e) {
            return null;
        }
    }

    private SharedPreferences getEncryptedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
}
