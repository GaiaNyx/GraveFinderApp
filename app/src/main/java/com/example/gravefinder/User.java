package com.example.gravefinder;

import java.util.ArrayList;

public class User {
    private String userId;
    private String email;
    private String phone;
    private String name;
    private String password;
    private ArrayList<Grave> myGraves;

    public User(String userId, String email, String phone, String name,
                String password, ArrayList<Grave> myGraves) {
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.myGraves = myGraves;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Grave> getMyGraves() {
        return myGraves;
    }

    public void setMyGraves(ArrayList<Grave> myGraves) {
        this.myGraves = myGraves;
    }

}
