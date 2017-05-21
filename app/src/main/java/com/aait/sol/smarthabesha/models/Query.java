package com.aait.sol.smarthabesha.models;

/**
 * Created by Sol on 5/7/2017.
 */

public class Query {

    /*
    * Action type of the transcription txt
    */
    private Actions.ActionType actionType = Actions.ActionType.DEFAULT;
    private String content = null;
    private String contactName = null;
    private String time = null;
    private String email = null;
    private String title = null;
    private String number = null;

    /*
    * Errors that will  found while processing the transcription
    */
    private Errors.ErrorType errorType = Errors.ErrorType.NO_ERROR;

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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contact) {
        this.contactName = contact;
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

    public Errors.ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(Errors.ErrorType errorType) {
        this.errorType = errorType;
    }
}
