package com.example.geosurvey.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Question {
    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;
    @SerializedName("radius")
    private double radius;
    @SerializedName("answers")
    private Set<Answer> answers = new LinkedHashSet<>();
    @SerializedName("geoLocalization")
    private GeoLocalization geoLocalization;

    public Question(String title, String content, double radius, List<String> answers, double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.radius = radius;
        answers.forEach(text -> this.answers.add(new Answer(text)));
        this.geoLocalization = new GeoLocalization(latitude, longitude);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public GeoLocalization getGeoLocalization() {
        return geoLocalization;
    }

    public void setGeoLocalization(GeoLocalization geoLocalization) {
        this.geoLocalization = geoLocalization;
    }


}
