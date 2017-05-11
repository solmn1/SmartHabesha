package com.aait.sol.smarthabesha.app_controller;

import com.aait.sol.smarthabesha.models.Query;

import java.sql.Time;

/**
 * Created by Sol on 5/7/2017.
 */

public class ApplicationController {
    public boolean sendMessage(Query query){
        return true;
    }

    public boolean call(Query query){
        return true;
    }
    public String getCurrentTime(){
        return null;
    }

    public boolean setAlarm(Query query){
        return true;
    }

    public boolean setReminder(Query query){
        return true;
    }

    public boolean sendEmail(Query query){
        return  true;
    }

    public boolean saveNewContact(Query query){
        return true;
    }
}
