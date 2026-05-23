package com.example.securepasswordmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.security.CryptoHelper;
import com.example.securepasswordmanager.security.SecurityManager;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.Executor;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPin;
    private Button btnUnlock, btnBiometric;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPin = findViewById(R.id.etPin);
        btnUnlock = findViewById(R.id.btnUnlock);
        btnBiometric = findViewById(R.id.btnBiometric);

        btnUnlock.setOnClickListener(v -> {
            String pin = etPin.getText().toString();
            if (pin.length() >= 4 && pin.length() <= 6) {
                loginWithPin(pin);
            } else {
                Toast.makeText(this, R.string.msg_invalid_pin, Toast.LENGTH_SHORT).show();
            }
        });

        setupBiometric();
        btnBiometric.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

    private void loginWithPin(String pin) {
        SecretKeySpec key = CryptoHelper.deriveKey(pin);
        SecurityManager.getInstance().setSecretKey(key);
        if (SecurityManager.getInstance().getStoredPin(this) == null) {
            SecurityManager.getInstance().savePinForBiometrics(this, pin);
        }
        
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void setupBiometric() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), getString(R.string.msg_auth_error, errString), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String storedPin = SecurityManager.getInstance().getStoredPin(getApplicationContext());
                if (storedPin != null) {
                    loginWithPin(storedPin);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msg_biometric_not_setup, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), R.string.msg_auth_failed, Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setNegativeButtonText(getString(R.string.biometric_negative_button))
                .build();
        if (SecurityManager.getInstance().getStoredPin(this) == null) {
            btnBiometric.setVisibility(android.view.View.GONE);
        }
    }
}
