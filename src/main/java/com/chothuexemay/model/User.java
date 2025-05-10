package com.chothuexemay.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String hoTen;
    private String role; // "admin" hoặc "user"
    private String soDienThoai;

    public User(int id, String username, String password, String hoTen, String role, String soDienThoai) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.hoTen = hoTen;
        this.role = role;
        this.soDienThoai = soDienThoai;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getRole() {
        return role;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
} 