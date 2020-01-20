package com.example.geosurvey.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Question implements Parcelable {
    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("radius")
    private double radius;
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    @SerializedName("geoLocalization")
    private GeoLocalization geoLocalization;

    public Question(String title, String content, double radius, List<String> answers, double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.radius = radius;
        answers.forEach(text -> this.answers.add(new Answer(text)));
        this.geoLocalization = new GeoLocalization(latitude, longitude);
    }

    @SerializedName("answers")
    private Set<Answer> answers = new HashSet<>();

    protected Question(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        content = in.readString();
        radius = in.readDouble();
        createdAt = new Date(in.readLong());
        geoLocalization = in.readParcelable(Question.class.getClassLoader());
        List<Answer> answersList = new ArrayList<>();
        in.readTypedList(answersList, Answer.CREATOR);
        answers.addAll(answersList);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(content);
        dest.writeDouble(radius);
        dest.writeLong(createdAt.getTime());
        dest.writeParcelable(geoLocalization, flags);
        dest.writeTypedList(new ArrayList<>(answers));
    }
}
