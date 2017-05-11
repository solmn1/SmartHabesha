package com.aait.sol.smarthabesha.models;

/**
 * Created by Sol on 5/7/2017.
 */

public class Query {
    private Actions.ActionType actionType;
    private String content = null;
    private String contact = null;
    private String time = null;
    private String email = null;
    private String title = null;
    private String number = null;

    public Query(Actions.ActionType actionType){
        this.actionType = actionType;
    }

    public Query(){}

    public void setNumber(String number){
        this.number = number;
    }

    public String getNumber(){
        return this.number;
    }
    public Actions.ActionType getActionType() {
        return actionType;
    }

    public void setActionType(Actions.ActionType actionType) {
        this.actionType = actionType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
