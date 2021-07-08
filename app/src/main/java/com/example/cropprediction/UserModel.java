package com.example.cropprediction;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class UserModel implements Serializable {
    String username;
    String email;
    String phone;
    boolean isEmailVerified;

    public UserModel( String username, String email, String phone, boolean isEmailVerified) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.isEmailVerified = isEmailVerified;

    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean isEmailVerified) {
        isEmailVerified = isEmailVerified;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }


    public UserModel() {

    }

    public UserModel(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
    }
}
