package com.danter.google.auth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by akshay.maniyar on 05/06/15.
 */
public class MailData {

    private Date timestamp;
    private String title;
    private List<String> bodyList = new ArrayList<String>();
    private List<String> participants = new ArrayList<String>();
    private String organiser;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getBodyList() {
        return bodyList;
    }

    public void setBodyList(List<String> bodyList) {
        this.bodyList = bodyList;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public void addParticipant(String participant){
        this.participants.add(participant);
    }

    public void addMessage(String message){
        this.bodyList.add(message);
    }
}
