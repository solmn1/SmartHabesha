package com.aait.sol.smarthabesha;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sol on 4/22/2017.
 */

public class PassiveListener {
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private RecorderListener recorderListener;
    public PassiveListener(){
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 2;
    }
    public void setListener( RecorderListener listener){
        recorderListener = listener;
    }
    public void startRecording(){
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,AudioFormat.CHANNEL_IN_MONO,RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        if(i==1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                sendData();
            }
        },"AudioRecorder Thread");

        recordingThread.start();
    }
    private void sendData(){
        byte data[] = new byte[bufferSize];
        while(isRecording){
            int read = recorder.read(data, 0, bufferSize);
            if(AudioRecord.ERROR_INVALID_OPERATION!= read){
                recorderListener.onRecordResult(data);
            }
        }

    }


    public void stopRecording(){
        if(null != recorder){
            isRecording = false;

            int i = recorder.getState();
            if(i==1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }
    }


}
