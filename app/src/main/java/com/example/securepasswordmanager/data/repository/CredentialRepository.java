package com.example.securepasswordmanager.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.securepasswordmanager.data.local.AppDatabase;
import com.example.securepasswordmanager.data.local.Credential;
import com.example.securepasswordmanager.data.local.CredentialDao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CredentialRepository {
    private final CredentialDao credentialDao;
    private final LiveData<List<Credential>> allCredentials;
    private final ExecutorService executorService;

    public CredentialRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        credentialDao = db.credentialDao();
        allCredentials = credentialDao.getAllCredentials();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Credential>> getAllCredentials() {
        return allCredentials;
    }

    public void insert(Credential credential) {
        executorService.execute(() -> credentialDao.insert(credential));
    }

    public void update(Credential credential) {
        executorService.execute(() -> credentialDao.update(credential));
    }

    public void delete(Credential credential) {
        executorService.execute(() -> credentialDao.delete(credential));
    }
}
