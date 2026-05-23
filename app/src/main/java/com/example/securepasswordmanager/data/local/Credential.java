package com.example.securepasswordmanager.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "credentials")
public class Credential {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String serviceName;
    private String username;
    private String password;
    private String category;
    private long createdAt;

    public Credential(String serviceName, String username, String password, String category) {
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
