package com.aait.sol.smarthabesha.speech_recognition;

/**
 * Created by Sol on 5/7/2017.
 */
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Recognizer {

    public interface Listener{
//        abstract void onRecordingBegin();
//        abstract void onRecordingDone();
        abstract void onError(Exception error);
        abstract void onPartialResult(String result);
        abstract void onFinalResult(String result);
        //abstract void onFinish(String reason);
        abstract void onReady(String reason);
        abstract void onNotReady(String reason);
        //abstract void onUpdateStatus();
        //abstract void onResult(ServerResponse response);
        abstract void onConnectedToServer();
        abstract void onClosedToServer(String reason);
    }

    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private WebSocketClient ws_client_websocket;
    private ServerInformation serverInfo;
    private String EOS = "EOS";
    private Listener listener;

    private AudioRecord recorder;
    private Thread recordingThread;
    private boolean isRecording;
    private int bufferSize;

    private boolean isConnected = false;
    public Recognizer(ServerInformation serverInfo, Listener listener){
        this.serverInfo = serverInfo;
        setListener(listener);
    }
    public Recognizer(Listener listener){
        setListener(listener);
        this.serverInfo = new ServerInformation();
    }

    private void setListener(Listener listener){
        this.listener = listener;
    }

    private void createRecorder(){
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat
                .CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2;

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSize);
    }
    public void startRecording(){
        connect();
        if(recorder == null){
            createRecorder();
        }
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                startSending();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }
    private void startSending(){
        byte sData[] = new  byte[bufferSize];
        while (isRecording) {
            int bytesRead = recorder.read(sData, 0, bufferSize);
            if (bytesRead > 0) {
                send(sData);
            }
        }
    }
    private void send(byte[] data){
        try{
            if(ws_client_websocket != null && isConnected){
                ws_client_websocket.send(data);
            }
        }catch (Exception e){
            e.printStackTrace();
            listener.onError(e);
        }
    }
    public void stopRecording(){
        if(ws_client_websocket != null && isConnected){
            ws_client_websocket.send(EOS);

        }
        if(recorder!= null){
            clean();
        }


    }
    private void connect(){
        URI uri;
        try{
            uri = new URI(serverInfo.getSpeechServerUrl());
        }catch (Exception e){
            e.printStackTrace();
            listener.onError(e);
            return;
        }

        ws_client_websocket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                isConnected = true;
                listener.onConnectedToServer();
            }

            @Override
            public void onMessage(String message) {

                handleResult(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                listener.onClosedToServer(reason);
                isConnected = false;
            }

            @Override
            public void onError(Exception ex) {
                listener.onError(ex);
            }
        };
        ws_client_websocket.connect();
    }
    private void handleResult(String msg){
         ServerResponse response = new ServerResponse(msg);
         if(response.getStatus() == ServerResponse.STATUS_NOT_AVAILABLE){
             ServerResponse.Message message = response.parseMesage();
             listener.onNotReady(message.getMessage());
         }
         else if(response.getStatus() == ServerResponse.STATUS_NO_SPEECH){
             listener.onNotReady("No speech input provided");
         }
         else if(response.getStatus() == ServerResponse.STATUS_SUCCESS){
             listener.onReady("Ready");
            if(response.isResult()){
                ServerResponse.Result result = response.parseResult();
                if(result.isFinal()){
                    listener.onFinalResult(result.getTranscript());
                }
                else{
                    listener.onPartialResult(result.getTranscript());
                }
            }
         }
    }
    private void clean(){
        if(ws_client_websocket != null){
            ws_client_websocket = null;
        }
        isRecording = false;
        int i = recorder.getState();
        if(i == 1)
            recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }
}
