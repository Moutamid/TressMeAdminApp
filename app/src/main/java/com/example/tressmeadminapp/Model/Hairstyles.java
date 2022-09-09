package com.example.tressmeadminapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Hairstyles implements Parcelable {

    private String id;
    private String name;
    private String imageUrl;

    private Hairstyles(){

    }

    public Hairstyles(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    protected Hairstyles(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Hairstyles> CREATOR = new Creator<Hairstyles>() {
        @Override
        public Hairstyles createFromParcel(Parcel in) {
            return new Hairstyles(in);
        }

        @Override
        public Hairstyles[] newArray(int size) {
            return new Hairstyles[size];
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
