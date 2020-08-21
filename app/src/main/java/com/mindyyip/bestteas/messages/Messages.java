package com.mindyyip.bestteas.messages;

public class Messages {
    private String message;
    Boolean isCreator;
    public Messages(String message, Boolean isCreator) {
        this.message = message;
        this.isCreator = isCreator;
    }
    public String getMessage() {return message;}
    public void setMessage() {this.message = message;}
    public Boolean getIsCreator() {return isCreator;}
    public void setIsCreator() {this.isCreator = isCreator;}
}
