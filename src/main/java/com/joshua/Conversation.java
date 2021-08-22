package com.joshua;

public class Conversation {
    private Participant[] participants;
    private Message[] messages;

    public Conversation(Participant[] participants, Message[] messages){
        this.participants = participants;
        this.messages = messages;
    }

    public Participant[] getParticipants(){
        return participants;
    }

    public Message[] getMessages(){
        return messages;
    }

    public void setParticipants(Participant[] participants){
        this.participants = participants;
    }

    public void setMessages(Message[] messages){
        this.messages = messages;
    }

}
