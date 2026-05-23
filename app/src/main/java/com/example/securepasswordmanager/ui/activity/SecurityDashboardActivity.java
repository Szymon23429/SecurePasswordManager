package com.example.securepasswordmanager.ui.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.security.CryptoHelper;
import com.example.securepasswordmanager.security.SecurityManager;
import com.example.securepasswordmanager.ui.viewmodel.MainViewModel;
import com.example.securepasswordmanager.util.PasswordGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

public class SecurityDashboardActivity extends AppCompatActivity {

    private TextView tvWeak, tvReused, tvStrong;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_dashboard);

        tvWeak = findViewById(R.id.tvWeakCount);
        tvReused = findViewById(R.id.tvReusedCount);
        tvStrong = findViewById(R.id.tvStrongCount);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getAllCredentials().observe(this, this::performAudit);
    }

    private void performAudit(List<Credential> credentials) {
        int weak = 0;
        int strong = 0;
        Map<String, Integer> passwordCounts = new HashMap<>();
        SecretKeySpec key = SecurityManager.getInstance().getSecretKey();

        for (Credential c : credentials) {
            try {
                String decryptedPass = CryptoHelper.decrypt(c.getPassword(), key);
                int strength = PasswordGenerator.calculateStrength(decryptedPass);
                
                if (strength <= 2) weak++;
                else if (strength >= 5) strong++;

                passwordCounts.put(decryptedPass, passwordCounts.getOrDefault(decryptedPass, 0) + 1);
            } catch (Exception ignored) {}
        }

        int reused = 0;
        for (int count : passwordCounts.values()) {
            if (count > 1) reused += count;
        }

        tvWeak.setText(String.valueOf(weak));
        tvReused.setText(String.valueOf(reused));
        tvStrong.setText(String.valueOf(strong));
    }
}