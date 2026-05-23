package com.example.securepasswordmanager.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.example.securepasswordmanager.R;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.security.CryptoHelper;
import com.example.securepasswordmanager.security.SecurityManager;
import com.example.securepasswordmanager.ui.adapter.CredentialAdapter;
import com.example.securepasswordmanager.ui.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements CredentialAdapter.OnItemClickListener {

    private MainViewModel viewModel;
    private CredentialAdapter adapter;
    private final Handler clipboardHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (SecurityManager.getInstance().isLocked()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.rvCredentials);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CredentialAdapter(this);
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getAllCredentials().observe(this, credentials -> adapter.setCredentials(credentials));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditCredentialActivity.class)));

        setupSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SecurityManager.getInstance().lock();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_audit) {
            startActivity(new Intent(this, SecurityDashboardActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onCopyClick(Credential credential) {
        try {
            String password = CryptoHelper.decrypt(credential.getPassword(), SecurityManager.getInstance().getSecretKey());
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();


            clipboardHandler.postDelayed(() -> {
                clipboard.setPrimaryClip(ClipData.newPlainText("", ""));
                Toast.makeText(this, R.string.clipboard_cleared, Toast.LENGTH_SHORT).show();
            }, 30000);

        } catch (Exception e) {
            Toast.makeText(this, R.string.msg_decrypt_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Credential credential) {
        viewModel.delete(credential);
        Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
    }
}
