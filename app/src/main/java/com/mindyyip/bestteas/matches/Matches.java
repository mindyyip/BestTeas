package com.mindyyip.bestteas.matches;

public class Matches {
    private String userId, name, profilePic;
    public Matches(String userId, String name, String profilePic) {
        this.userId = userId;
        this.name = name;
        this.profilePic = profilePic;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProfilePic() {
        return profilePic;
    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
