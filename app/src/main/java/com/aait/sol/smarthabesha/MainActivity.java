package com.aait.sol.smarthabesha;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class MainActivity extends Activity{
    private EditText serverAddr;
    private Button connectBtn;
    private Button btnStart;
    private Button btnStop;
    private TextView status;
    private URI uri;
    private WebSocketClient mWebSocketClient;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mAudioRecord = null;
    private Thread mRecordingThread = null;
    private boolean mIsRecording = false;
    private int mBufferSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverAddr = (EditText) findViewById(R.id.serverAddress);
        connectBtn = (Button) findViewById(R.id.connectBtn);
        status = (TextView) findViewById(R.id.status);
        btnStart = (Button) findViewById(R.id.btnsatrt);
        btnStop = (Button) findViewById(R.id.btnstop);
        btnStop.setEnabled(false);
        mBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat
                .CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2;

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                mBufferSize);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    connectWebSocket();
                    startRecording();
                }catch (Exception ex){ status.setText(status.getText() + "\n" +ex.getMessage().toString());}

                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    stopRecording();
                }catch (Exception ex){

                    status.setText(status.getText() + "\n" +"stoping:" +ex.getMessage());
                }

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                try{
                    mWebSocketClient.send("EOS");
                }catch (Exception ex){
                    status.setText(status.getText() + "\n sending:Eos:" + ex.getMessage());
                }

            }
        });

    }
    private void startRecording() {
        mAudioRecord.startRecording();
        mIsRecording = true;
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                readData();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }
    private void readData() {
        byte sData[] = new  byte[mBufferSize];
        while (mIsRecording) {
            int bytesRead = mAudioRecord.read(sData, 0, mBufferSize);
            if (bytesRead > 0) {
                try {
                    mWebSocketClient.send(sData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void stopRecording(){
        mIsRecording = false;
        mAudioRecord.stop();
    }
    public void connectWebSocket(){
        try{
            if(serverAddr.getText().toString().equals("")){
                uri = new URI("ws://192.168.43.12:8080/client/ws/speech" + "?content-type=audio/x-raw,+layout=(string)interleaved,+rate=(int)16000,+format=(string)S16LE,+channels=(int)1"); }
            else{
                uri = new URI(serverAddr.getText().toString());
            }

        }catch (Exception ex){
            status.setText(status.getText() + "\n" + ex.getMessage());
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView status = (TextView) findViewById(R.id.status);
                        status.setText("Connection Opened");
                    }
                });

//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Toast.makeText(MainActivity.this,"Message recived", Toast.LENGTH_LONG).show();
                            EditText result = (EditText) findViewById(R.id.serverAddress);
                            JSONObject jObj = new JSONObject(message);
                            int _status = jObj.getInt("status");
                            String _transcript = jObj.getJSONObject("result").getJSONArray("hypotheses").getJSONObject(0).getString("transcript");
                            boolean _isFinal = jObj.getJSONObject("result").getBoolean("final");
                            result.setText( _transcript);
                        }catch (Exception ex){}

                    }
                });
            }


            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView status = (TextView) findViewById(R.id.status);
                        status.setText("Connection Closed");
                    }
                });
            }
            @Override
            public void onError(Exception e) {
                final Exception exx = e;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView status = (TextView) findViewById(R.id.status);
                        status.setText(status.getText()+"\n" + exx.getMessage().toString());
                        
                    }
                });
            }
        };
        try{
            mWebSocketClient.connect();
        }catch (Exception ex){
            status.setText(status.getText() + "\n connecting:" + ex.getMessage());
        }

    }


}
