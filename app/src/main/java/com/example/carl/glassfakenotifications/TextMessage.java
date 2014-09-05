package com.example.carl.glassfakenotifications;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Carl on 2014-09-02.
 */
public class TextMessage {
    private String sender;
    private String message;
    private String timestamp;
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm");

    public TextMessage(String sender, String message){
        this.sender = sender;
        this.message = message;
        this.timestamp = timeFormat.format(new Date());
    }

    public String getSender(){
        return sender;
    }

    public String getMessage(){
        return message;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setSender(String newSender){
        sender = newSender;
    }

    public void setMessage(String newMessage){
        message = newMessage;
    }


}
