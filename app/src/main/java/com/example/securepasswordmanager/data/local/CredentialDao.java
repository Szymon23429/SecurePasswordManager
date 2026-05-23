package com.example.securepasswordmanager.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CredentialDao {
    @Insert
    void insert(Credential credential);

    @Update
    void update(Credential credential);

    @Delete
    void delete(Credential credential);

    @Query("SELECT * FROM credentials ORDER BY id DESC")
    LiveData<List<Credential>> getAllCredentials();
}
