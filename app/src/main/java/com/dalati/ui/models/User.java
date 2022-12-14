package com.dalati.ui.models;

import java.io.Serializable;

public class User implements Serializable {
    String id, name, email, phone, user_type, token, gender, age,imgUrl;

    public User() {
    }

    public User(String id, String name, String email, String phone, String user_type, String token, String gender, String age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.user_type = user_type;
        this.token = token;
        this.gender = gender;
        this.imgUrl = "Default";
        this.age = age;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getage() {
        return age;
    }

    public void setage(String age) {
        this.age = age;
    }
}
