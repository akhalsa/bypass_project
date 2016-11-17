package com.bypassmobile.octo.model;


import com.google.gson.annotations.SerializedName;


public class User {

    @SerializedName("login")
    private final String name;

    @SerializedName("avatar_url")
    private final String profileURL;

    private final String id;

    public User(String name, String profileURL, String id) {
        this.name = name;
        this.profileURL = profileURL;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public String getId(){
        return id;
    }
}
