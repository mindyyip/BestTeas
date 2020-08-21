package com.mindyyip.bestteas.cards;

public class Cards {
    private String userId, name, profilePicUrl, bio;
    public Cards(String userId, String name, String profilePicUrl, String bio) {
        this.userId = userId;
        this.name = name;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
    }
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getProfilePicUrl() {
        return profilePicUrl;
    }
    public String getBio() {return bio;}
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
    public void setBio(String bio) {this.bio = bio;}


}
