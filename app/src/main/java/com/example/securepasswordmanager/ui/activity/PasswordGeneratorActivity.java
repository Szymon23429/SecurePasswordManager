package com.example.securepasswordmanager.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.util.PasswordGenerator;
import com.google.android.material.checkbox.MaterialCheckBox;

public class PasswordGeneratorActivity extends AppCompatActivity {

    private TextView tvGenerated, tvLengthValue;
    private SeekBar sbLength;
    private MaterialCheckBox cbSymbols, cbNumbers;
    private Button btnGenerate, btnUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);

        tvGenerated = findViewById(R.id.tvGeneratedPassword);
        tvLengthValue = findViewById(R.id.tvLengthValue);
        sbLength = findViewById(R.id.sbLength);
        cbSymbols = findViewById(R.id.cbSymbols);
        cbNumbers = findViewById(R.id.cbNumbers);
        btnGenerate = findViewById(R.id.btnGenerateNow);
        btnUse = findViewById(R.id.btnUsePassword);

        sbLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int length = Math.max(progress, 4);
                tvLengthValue.setText(String.valueOf(length));
                generate();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnGenerate.setOnClickListener(v -> generate());
        btnUse.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("generated_password", tvGenerated.getText().toString());
            setResult(RESULT_OK, result);
            finish();
        });

        generate();
    }

    private void generate() {
        int length = sbLength.getProgress();
        if (length < 4) length = 4;
        String password = PasswordGenerator.generate(length, cbSymbols.isChecked(), cbNumbers.isChecked());
        tvGenerated.setText(password);
    }
}
