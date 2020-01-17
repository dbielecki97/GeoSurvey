package com.example.geosurvey.model;

import com.google.gson.annotations.SerializedName;

public class Answer {
    @SerializedName("id")
    private Long id;
    @SerializedName("text")
    private String text;
    @SerializedName("count")
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
