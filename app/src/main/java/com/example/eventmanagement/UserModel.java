package com.example.eventmanagement;

public class UserModel {
    String owner_name, number, email;
    private String userId;

    public UserModel(String userId) {
        this.userId = userId;
    }

    public UserModel(String owner_name, String number, String email) {
        this.owner_name = owner_name;
        this.number = number;
        this.email = email;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int email() {
        return 0;
    }
/*
    public UserModel(String name, String number, String email) {
        this.owner_name = name;
        this.number = number;

        this.email = email;
    }

    public String getName() {
        return owner_name;
    }

    public void setName(String name) {
        this.owner_name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public String getEmail() {
        return email;
    }

     */

}
