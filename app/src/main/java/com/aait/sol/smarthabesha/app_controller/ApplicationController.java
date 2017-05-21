package com.aait.sol.smarthabesha.app_controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.aait.sol.smarthabesha.models.Query;

import java.util.ArrayList;
import java.util.Date;
import android.Manifest;
import android.telephony.SmsManager;


/**
 * Created by Sol on 5/7/2017.
 */

public class ApplicationController {
    ArrayList<String> contactNames;
    ArrayList<String> contactNumbers;
    Context context;
    public ApplicationController(Context context){
        this.context = context;
    }
    public boolean sendMessage(Query query){
        SmsManager sendMessage = SmsManager.getDefault();
        sendMessage.sendTextMessage(query.getNumber(),null, query.getContent(), null, null);
        return true;
    }

    public boolean call(Query query){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+ query.getNumber()));
        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        context.startActivity(intent);
        return true;
    }
    public String getCurrentTime(){
        Date date = new Date();

        return date.getHours()%12 + ":" + date.getMinutes();
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

    public String getContactNumber(String name){
        return null;
    }
    public String getCorrectName(String name){
        return name;
    }
    public boolean saveNewContact(Query query){
        return true;
    }
    public ArrayList<String> getContact(){
        Uri simUri = Uri.parse("content://icc/adn");

        Cursor cursorSim = context.getContentResolver().query(simUri, null, null,null, null);

        while (cursorSim.moveToNext()) {
            contactNames.add(cursorSim.getString(cursorSim.getColumnIndex("name")));
            //listContactId.add(cursorSim.getString(cursorSim.getColumnIndex("_id")));
            contactNames.add(cursorSim.getString(cursorSim.getColumnIndex("number")));
        }
        return contactNames;
    }



}
