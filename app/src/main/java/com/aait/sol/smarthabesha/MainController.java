package com.aait.sol.smarthabesha;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aait.sol.smarthabesha.speech_recognition.Recognizer;

import ee.ioc.phon.android.speechutils.view.MicButton;

/**
 * Created by Sol on 5/5/2017.
 */

public class MainController extends Activity implements Recognizer.Listener{
    private MicButton btnStartStop;
    private boolean mIsRecording = false;
    private Recognizer speechRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStartStop = (MicButton) findViewById(R.id.btnStartStop);
        btnStartStop.setState(MicButton.State.INIT);
        speechRecognizer  = new Recognizer(MainController.this);
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
}
