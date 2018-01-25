package com.example.ade.slametinpolisiapp1.Model;

/**
 * Created by Ade on 18/01/2018.
 */

public class User {
    private String email,password,nama,phone;

    public User() {
    }

    public User(String email, String password, String nama, String phone) {
        this.email = email;
        this.password = password;
        this.nama = nama;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
