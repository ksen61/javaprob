package com.example.treker;

// Класс для хранения данных пользователя
public class UserProfile {
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String role;


    public UserProfile(String name, String surname, String email, String phone, String role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // Геттеры и сеттеры для каждого поля
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}