package com.example.securepasswordmanager.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.data.repository.CredentialRepository;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final CredentialRepository repository;
    private final LiveData<List<Credential>> allCredentials;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new CredentialRepository(application);
        allCredentials = repository.getAllCredentials();
    }

    public LiveData<List<Credential>> getAllCredentials() {
        return allCredentials;
    }

    public void insert(Credential credential) {
        repository.insert(credential);
    }

    public void update(Credential credential) {
        repository.update(credential);
    }

    public void delete(Credential credential) {
        repository.delete(credential);
    }
}
