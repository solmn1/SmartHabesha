package com.aait.sol.smarthabesha;

/**
 * Created by Sol on 5/7/2017.
 */

public class Recognizer {
    public interface Listener{
        abstract void onRecordingBegin();
        abstract void onRecordingDone();
        abstract void onError(Exception error);
        abstract void onPartialResult(String result);
        abstract void onFinalResult(String result);
        abstract void onFinish(String reason);
        abstract void onReady(String reason);
        abstract void onNotReady(String reason);
        abstract void onUpdateStatus();
    }

}
