package com.meuprojeto.model;
import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private Integer papeisId;
    private LocalDateTime createdAt;

    public User() {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.papeisId = papeisId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getPapeisId() {
        return papeisId;
    }

    public void setPapeisId(Integer papeisId) {
        this.papeisId = papeisId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
