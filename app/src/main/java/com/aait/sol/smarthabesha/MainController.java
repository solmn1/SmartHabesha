package com.aait.sol.smarthabesha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aait.sol.smarthabesha.app_controller.ApplicationController;
import com.aait.sol.smarthabesha.models.Actions;
import com.aait.sol.smarthabesha.models.Errors;
import com.aait.sol.smarthabesha.models.Input;
import com.aait.sol.smarthabesha.models.Query;
import com.aait.sol.smarthabesha.nlp.NlpController;
import com.aait.sol.smarthabesha.speech_recognition.Recognizer;

import ee.ioc.phon.android.speechutils.view.MicButton;
import com.aait.sol.smarthabesha.test.*;



/**
 * Created by Sol on 5/5/2017.
 */

public class MainController extends Activity implements Recognizer.Listener{

    private boolean mIsRecording = false;
    private Input.InputType currentInputType = Input.InputType.FIRST;
    private Actions.ActionType currentActionType = Actions.ActionType.DEFAULT;

    private MicButton btnStartStop;
    private Button switchBtn;
    private EditText resultEditText;
    private LinearLayout currentLayout;

    private Query appQuery;
    private Recognizer speechRecognizer;
    private NlpController nlp;
    private ApplicationController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nlp = new NlpController();
        controller = new ApplicationController(this);
        speechRecognizer  = new Recognizer(MainController.this);

        currentLayout = (LinearLayout) findViewById(R.id.defaultLayout);
        resultEditText = (EditText) findViewById(R.id.result);
        resultEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String trans = resultEditText.getText().toString();
                        if(trans.contains(".")){
                            if(trans.lastIndexOf(".") == trans.length()-1){
                                String transcription = trans.replace(".","");
                                handleTranscription(transcription);
                            }
                        }



                    }
                });
            }
        });
        final Intent i = new Intent(this, MainActivity.class);
        btnStartStop = (MicButton) findViewById(R.id.btnStartStop);
        btnStartStop.setState(MicButton.State.INIT);
        switchBtn = (Button) findViewById(R.id.switchBtn);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(i);

            }
        });
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsRecording){
                    speechRecognizer.stopRecording();
                    btnStartStop.setState(MicButton.State.TRANSCRIBING);
                    mIsRecording = false;
                }
                else{
                    btnStartStop.setState(MicButton.State.LISTENING);
                    speechRecognizer.startRecording();
                    mIsRecording = true;
                }
            }
        });
    }
    @Override
    public void onError(final Exception error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView co = (TextView) findViewById(R.id.conn);
                co.setText(co.getText()+"\n" + error.getMessage());
            }
        });
    }

    @Override
    public void onPartialResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText resultEdit = (EditText) findViewById(R.id.result);
                resultEdit.setText(result);
            }
        });
    }

    @Override
    public void onFinalResult(final String rst) {
        btnStartStop.setState(MicButton.State.INIT);
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               EditText resultEdit = (EditText) findViewById(R.id.result);
               resultEdit.setText(rst);
           }
       });
    }

    @Override
    public void onReady(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView conneClose = (TextView) findViewById(R.id.status);
                conneClose.setText(reason);
            }
        });
    }

    @Override
    public void onNotReady(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView conneClose = (TextView) findViewById(R.id.status);
                conneClose.setText(reason);
            }
        });
    }

    @Override
    public void onConnectedToServer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView conneClose = (TextView) findViewById(R.id.conn);
                conneClose.setText("connected");
            }
        });
    }

    @Override
    public void onClosedToServer(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView conneClose = (TextView) findViewById(R.id.conn);
                conneClose.setText("Closed: " + reason);
            }
        });
    }

    public void handleTranscription(String result){
                Query query  = nlp.getQuery(result, currentActionType, currentInputType);
               if(query.getErrorType() != Errors.ErrorType.UNKNOWN_COMMAND){}
                if(currentActionType != query.getActionType()){
                    prepareGUI(query.getActionType());
                    currentActionType = query.getActionType();
                }
                if(currentActionType == Actions.ActionType.TIME){

                    TextView time = (TextView) findViewById(R.id.timeView);
                    time.setText(time.getText()+ controller.getCurrentTime());
                    currentActionType = Actions.ActionType.DEFAULT;
                    currentInputType = Input.InputType.FIRST;
                }
                else if(currentActionType == Actions.ActionType.CALL){

                    /*
                    * the transcription is valid and all required info is set
                    */
                    if(query.getErrorType() == Errors.ErrorType.NO_ERROR){

                        /*
                        * Contact name is set
                        */
                        if(query.getContactName() != null){
                            String number = controller.getContactNumber(query.getContactName());

                            /*
                            * the contact is exist
                            */
                            if(number != null){
                                appQuery = new Query();
                                appQuery.setNumber(number);
                                TextView view = (TextView) findViewById(R.id.shortCallNumber);
                                view.setText(controller.getCorrectName(query.getContactName()) + ": " + number);
                                controller.call(appQuery);
                                currentActionType = Actions.ActionType.DEFAULT;
                                currentInputType = Input.InputType.FIRST;
                                return;
                            }
                            else {
                                reportError(Errors.ErrorType.CONTACT_NOT_FOUND);
                            }
                        }

                        /*
                        * the phone number is set
                        */
                        else if(query.getNumber() != null){
                            appQuery = new Query();
                            appQuery.setNumber(query.getNumber());
                            TextView view = (TextView) findViewById(R.id.shortCallNumber);
                            view.setText("new contact :" + query.getNumber());
                            controller.call(appQuery);
                            currentActionType = Actions.ActionType.DEFAULT;
                            currentInputType = Input.InputType.FIRST;
                            return;
                        }
                        else {
                            currentInputType = Input.InputType.SECOND;
                        }

                    }

                    /*
                    * the trasnall information is not set. show to the user that  need more info
                    * and ask to re inter the info.
                    */
                    else {
                        reportError(query.getErrorType());
                        currentInputType = Input.InputType.SECOND;
                    }


                }
                else if(currentActionType == Actions.ActionType.MESSAGE){
                     if(currentInputType == Input.InputType.FIRST){
                         appQuery = new Query();
                         currentInputType = Input.InputType.SECOND;
                     }
                     else if(currentInputType == Input.InputType.SECOND){
                         appQuery.setNumber(query.getNumber());
                         currentInputType = Input.InputType.THIRD;
                     }
                     else if(currentInputType == Input.InputType.THIRD){
                         appQuery.setContent(query.getContent());
                         controller.sendMessage(appQuery);
                         currentActionType = Actions.ActionType.DEFAULT;
                         currentInputType = Input.InputType.FIRST;
                     }
                }
                else if(currentActionType == Actions.ActionType.EMAIL){

                }
                else if(currentActionType == Actions.ActionType.ALARM){

                }
                else if(currentActionType == Actions.ActionType.REMINDER){

                }
                else if(currentActionType == Actions.ActionType.NEW_CONTACT){

                }
                else{

                }



    }
    private void prepareGUI(Actions.ActionType action){
        if(action == Actions.ActionType.TIME){
            currentActionType = Actions.ActionType.TIME;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.time);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.CALL){
            currentActionType = Actions.ActionType.CALL;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.call);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.MESSAGE){
            currentActionType = Actions.ActionType.MESSAGE;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.message);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.EMAIL){
            currentActionType = Actions.ActionType.MESSAGE;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.email);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.ALARM){
            currentActionType = Actions.ActionType.ALARM;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.alarm);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.REMINDER){
            currentActionType = Actions.ActionType.REMINDER;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.reminder);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else if(action == Actions.ActionType.NEW_CONTACT){
            currentActionType = Actions.ActionType.NEW_CONTACT;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.newNumber);
            currentLayout.setVisibility(View.VISIBLE);
        }
        else{
            currentActionType = Actions.ActionType.DEFAULT;
            currentLayout.setVisibility(View.GONE);
            currentLayout = (LinearLayout) findViewById(R.id.defaultLayout);
            currentLayout.setVisibility(View.VISIBLE);
        }
    }
    public void reportError(Errors.ErrorType errorType){

    }

    public void handleCall(Query query){
        if(query.getContactName() != null){
            String number = controller.getContactNumber(query.getContactName());
            if(number != null){
                appQuery = new Query();
                appQuery.setNumber(number);
                TextView view = (TextView) findViewById(R.id.shortCallNumber);
                view.setText(controller.getCorrectName(query.getContactName()) + ": " + number);
                controller.call(appQuery);
                return;
            }
            else {
                reportError(Errors.ErrorType.CONTACT_NOT_FOUND);
            }
        }
        else if(query.getNumber() != null){
            appQuery = new Query();
            appQuery.setNumber(query.getNumber());
            TextView view = (TextView) findViewById(R.id.shortCallNumber);
            view.setText("new contact :" + query.getNumber());
            controller.call(appQuery);
            return;

        }

    }

}
