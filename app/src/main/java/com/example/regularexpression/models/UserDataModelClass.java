package com.example.regularexpression.models;

import java.io.Serializable;

public class UserDataModelClass implements Serializable {
    String email, url, phone, name;
    public UserDataModelClass() {

    }

    public UserDataModelClass(String email, String url, String phone, String name) {
        this.email = email;
        this.url = url;
        this.phone = phone;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
