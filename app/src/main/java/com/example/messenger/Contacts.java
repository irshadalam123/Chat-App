package com.example.messenger;

public class Contacts {


    public String userName;
    public String Profile_Image;
    public String userIdKey;


    public Contacts(){
    }

    public Contacts(String name, String profile_Image) {
        this.userName = name;
        this.Profile_Image = profile_Image;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        this.Profile_Image = profile_Image;
    }
}
