package com.example.tressmeadminapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Sponsored implements Parcelable {

    private String id;
    private String name;
    private String imageUrl;

    private Sponsored(){

    }

    public Sponsored(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    protected Sponsored(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Sponsored> CREATOR = new Creator<Sponsored>() {
        @Override
        public Sponsored createFromParcel(Parcel in) {
            return new Sponsored(in);
        }

        @Override
        public Sponsored[] newArray(int size) {
            return new Sponsored[size];
        }
    };

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(imageUrl);
    }
}
