package com.aait.sol.smarthabesha;

/**
 * Created by Sol on 5/7/2017.
 */

public class ServerInformation {
    private String ipAddress="192.168.43.12";
    private String app_speech="client/ws/speech";
    private String app_status="client/ws/status";
    private int port=8080;

    public ServerInformation(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public ServerInformation(){}

    public void setAddr(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setAppSpeech(String app_speech){
        this.app_speech = app_speech;
    }

    public void setAppStatus(String app_status){
        this.app_status = app_status;
    }

    public String getAddr(){
        return this.ipAddress;
    }

    public int getPort(){
        return this.port;
    }

    public String getSpeechServerUrl(){
        return "ws://"+this.ipAddress+":"+this.port+"/"+app_speech+
            "?content-type=audio/x-raw,+layout=(string)interleaved,+rate=(int)16000,+format=(string)S16LE,+channels=(int)1";
    }

    public String getStatusServerUrl(){
        return "ws://"+this.ipAddress+":"+this.port+"/"+app_status;
    }
}
