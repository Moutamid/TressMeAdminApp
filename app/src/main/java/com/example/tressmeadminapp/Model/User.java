package com.example.tressmeadminapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String uId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String userType;
    private String bio;

    private String imageUrl;

    public User() {
    }

    public User(String uId, String userType, String name, String email, String phone,
                String password, String imageUrl, String bio) {
        this.uId = uId;
        this.userType = userType;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    protected User(Parcel in) {
        uId = in.readString();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        password = in.readString();
        userType = in.readString();
        imageUrl = in.readString();
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

        parcel.writeString(uId);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(password);
        parcel.writeString(userType);
        parcel.writeString(imageUrl);
    }

}
