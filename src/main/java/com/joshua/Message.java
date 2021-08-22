package com.joshua;

public class Message {
    private String sender_name;
    private int timestamp;
    private String content;
    private String type;
    private boolean isUnsent;

    public Message(String sender_name, int timestamp, String content, String type, boolean isUnsent){
        this.sender_name = sender_name;
        this.timestamp = timestamp;
        this.content = content;
        this.type = type;
        this.isUnsent = isUnsent;
    }

    public String getSenderName(){
        return sender_name;
    }

    public int getTimestamp(){
        return timestamp;
    }    

    public String getContent(){
        return content;
    }

    public String getType(){
        return type;
    }

    public boolean getIsUnsent(){
        return isUnsent;
    }

    public void setSenderName(String sender_name){
        this.sender_name = sender_name;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setIsUnsent(boolean isUnsent){
        this.isUnsent = isUnsent;
    }

}
