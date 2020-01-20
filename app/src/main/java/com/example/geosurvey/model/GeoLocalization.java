package com.example.geosurvey.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GeoLocalization implements Parcelable {
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("id")
    private Long id;

    public GeoLocalization(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static final Creator<GeoLocalization> CREATOR = new Creator<GeoLocalization>() {
        @Override
        public GeoLocalization createFromParcel(Parcel in) {
            return new GeoLocalization(in);
        }

        @Override
        public GeoLocalization[] newArray(int size) {
            return new GeoLocalization[size];
        }
    };

    protected GeoLocalization(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
    }
}
