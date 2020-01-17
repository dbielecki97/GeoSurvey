package com.example.geosurvey.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class User implements Parcelable {
    @SerializedName("password")
    private String password;

    @SerializedName("username")
    private String username;


    @SerializedName("email")
    private String email;

    @SerializedName("active")
    private boolean active;

    @SerializedName("questions")
    private Set<Question> questions;

    public User(String username, String password, String email) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.active = true;
    }

    private User(Parcel in) {
        password = in.readString();
        username = in.readString();
        email = in.readString();
        active = in.readByte() != 0;
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(String username, String password) {

        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @SerializedName("id")
    private Long id;

    @SerializedName("roles")
    private Set<Role> roles;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(password);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeByte(active ? (byte) 1 : (byte) 0);
        dest.writeLong(id);
    }
}
