package com.joshua;

public class ParticipantCount {
    private String name;
    private int messageCount;
    private int wordCount;
    private int charCount;

    public ParticipantCount(String name, int messageCount, int wordCount, int charCount){
        this.name = name;
        this.messageCount = messageCount;
        this.wordCount = wordCount;
        this.charCount = charCount;
    }

    public String getName(){
        return name;
    }

    public int getMessageCount(){
        return messageCount;
    }

    public int getWordCount(){
        return wordCount;
    }

    public int getCharCount(){
        return charCount;
    }

    public void setName(String name){
        this.name =name;
    }

    public void setMessageCount(int messageCount){
        this.messageCount = messageCount;
    }

    public void setWordCount(int wordCount){
        this.wordCount = wordCount;
    }

    public void setCharCount(int charCount){
        this.charCount = charCount;
    }

    public void addMessageCount(int messageCountAddition){
        this.messageCount += messageCountAddition;
    }

    public void addWordCount(int wordCountAddition){
        this.wordCount += wordCountAddition;
    }

    public void addCharCount(int charCountAddition){
        this.charCount += charCountAddition;
    }

}
