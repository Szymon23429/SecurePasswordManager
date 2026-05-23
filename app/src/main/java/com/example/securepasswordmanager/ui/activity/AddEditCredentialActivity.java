package com.example.securepasswordmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.security.CryptoHelper;
import com.example.securepasswordmanager.security.SecurityManager;
import com.example.securepasswordmanager.ui.viewmodel.MainViewModel;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import javax.crypto.spec.SecretKeySpec;

public class AddEditCredentialActivity extends AppCompatActivity {

    private TextInputEditText etService, etUsername, etPassword;
    private AutoCompleteTextView actvCategory;
    private LinearProgressIndicator progressStrength;
    private TextView tvStrength;
    private Button btnSave, btnGenerate;
    private MainViewModel viewModel;
    private static final int REQUEST_GENERATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_credential);

        etService = findViewById(R.id.etServiceName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        actvCategory = findViewById(R.id.actvCategory);
        progressStrength = findViewById(R.id.progressStrength);
        tvStrength = findViewById(R.id.tvStrength);
        btnSave = findViewById(R.id.btnSave);
        btnGenerate = findViewById(R.id.btnGenerate);

        setupCategoryDropdown();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                updateStrength(s.toString());
            }
        });

        btnSave.setOnClickListener(v -> saveCredential());
        btnGenerate.setOnClickListener(v -> {
            Intent intent = new Intent(this, PasswordGeneratorActivity.class);
            startActivityForResult(intent, REQUEST_GENERATE);
        });
    }

    private void setupCategoryDropdown() {
        String[] categories = new String[]{
                getString(R.string.category_work),
                getString(R.string.category_social),
                getString(R.string.category_banking),
                getString(R.string.category_personal),
                getString(R.string.category_other)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
        actvCategory.setText(categories[4], false);
    }

    private void updateStrength(String password) {
        int strength = com.example.securepasswordmanager.util.PasswordGenerator.calculateStrength(password);
        int labelResId;
        int color;
        if (strength <= 2) {
            labelResId = R.string.strength_weak;
            color = ContextCompat.getColor(this, R.color.strength_weak);
        } else if (strength <= 4) {
            labelResId = R.string.strength_medium;
            color = ContextCompat.getColor(this, R.color.strength_medium);
        } else {
            labelResId = R.string.strength_strong;
            color = ContextCompat.getColor(this, R.color.strength_strong);
        }
        tvStrength.setText(getString(R.string.strength_label, getString(labelResId)));
        tvStrength.setTextColor(color);
        progressStrength.setProgress(strength);
        progressStrength.setIndicatorColor(color);
    }

    private void saveCredential() {
        String service = etService.getText().toString().trim();
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String category = actvCategory.getText().toString();

        if (service.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, R.string.msg_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SecretKeySpec key = SecurityManager.getInstance().getSecretKey();
            String encService = CryptoHelper.encrypt(service, key);
            String encUser = CryptoHelper.encrypt(user, key);
            String encPass = CryptoHelper.encrypt(pass, key);

            Credential credential = new Credential(encService, encUser, encPass, category);
            viewModel.insert(credential);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_encryption_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GENERATE && resultCode == RESULT_OK && data != null) {
            String generated = data.getStringExtra("generated_password");
            etPassword.setText(generated);
        }
    }
}
