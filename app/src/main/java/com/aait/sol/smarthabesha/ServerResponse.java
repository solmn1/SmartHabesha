package com.aait.sol.smarthabesha;

import android.webkit.WebResourceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sol on 5/7/2017.
 */

public class ServerResponse {
    // Usually used when recognition results are sent.
    public static final int STATUS_SUCCESS = 0;

    // Audio contains a large portion of silence or non-speech.
    public static final int STATUS_NO_SPEECH = 1;

    // Recognition was aborted for some reason.
    public static final int STATUS_ABORTED = 2;

    // No valid frames found before end of stream.
    public static final int STATUS_NO_VALID_FRAMES = 5;

    // Used when all recognizer processes are currently in use and recognition cannot be performed.
    public static final int STATUS_NOT_AVAILABLE = 9;

    private JSONObject json ;
    private int status;

    public ServerResponse(String msg){
        try{
            json = new JSONObject(msg);
            status = json.getInt("status");
        }catch (Exception e){

        }
    }
    public Result parseResult(){
        Result result = null;
        try{
            result = new Result(json.getJSONObject("result"));
        }catch (Exception ex){}
        return result;
    }
    public Message parseMesage(){
        Message msg = null;
        try {
            return new Message(json.getString("message"));
        } catch (JSONException e) {

        }
        return msg;
    }
    public int getStatus(){
        return this.status;
    }

    public boolean isResult(){
        return json.has("result");
    }

    public static class Result{
        private  JSONObject mResult = null;
        private String transcript;
        public Result(JSONObject result){
            mResult = result;
        }

        public String getTranscript(){
            try{
                transcript = mResult.getJSONArray("hypotheses").getJSONObject(0).getString("transcript");
            }catch (Exception ex){}

            return  transcript;
        }
        public boolean isFinal(){
            return mResult.optBoolean("final", false);
        }
    }
    public static class Message {
        private final String mMessage;

        public Message(String message)  {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }
}
