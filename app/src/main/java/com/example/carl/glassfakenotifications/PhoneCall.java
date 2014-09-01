package com.example.carl.glassfakenotifications;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.glass.app.Card;
import com.google.android.glass.view.WindowUtils;

/**
 * Created by Carl on 2014-08-28.
 */
public class PhoneCall extends Activity{

    private String caller;
    private String number;
    private SoundPool mSoundPool;
    private int mIncomingCall;
    private int soundAccept;
    private int soundDecline;
    private int stopRingtoneID;
    private Window window;
    private boolean voiceCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        caller = intent.getStringExtra("caller");
        number = intent.getStringExtra("number");
        System.out.println("Caller: " + caller);

        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mIncomingCall = mSoundPool.load(getApplicationContext(), R.raw.sound_call_incoming_ring, 1);
        soundAccept = mSoundPool.load(getApplicationContext(), R.raw.sound_call_start, 1);
        soundDecline = mSoundPool.load(getApplicationContext(), R.raw.sound_call_stop, 1);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        voiceCommand = getIntent().getBooleanExtra("voicecommand", false);
        if(voiceCommand) {
            window.requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        }

        Card card = new Card(getApplicationContext());

        card.setText(caller);
        card.setFootnote(number);
        setContentView(card.getView());
        final Handler mHandler = new Handler();
        final Runnable mDelayedRingtone = new Runnable() {
            int counter = 0;
            @Override
            public void run() {
                counter++;

                if (counter == 1) {
                    boolean ret = mHandler.postDelayed(this, 500);
                    if (ret==false) System.out.print("mHandler.postAtTime FAILED!");
                } else {
                    stopRingtoneID = mSoundPool.play(mIncomingCall, 1, 1, 1, -1, 1);
                }
            }
        };
        mDelayedRingtone.run();
        broadcastLog("starting phone call from: " + caller);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            openOptionsMenu();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void playSound(int soundId) {
        mSoundPool.play(soundId,
                1 /* leftVolume */,
                1 /* rightVolume */,
                1,
                0 /* loop */,
                1 /* rate */);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            getMenuInflater().inflate(R.menu.phonecall, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        broadcastLog("user opened options menu");
        getMenuInflater().inflate(R.menu.phonecall, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()){
            case R.id.accept:
                //do stuff when they accept the call
                broadcastLog("user accepted call");
                mSoundPool.stop(stopRingtoneID);
                playSound(soundAccept);
                return true;
            case R.id.decline:
                broadcastLog("user declined call");
                mSoundPool.stop(stopRingtoneID);
                playSound(soundDecline);
                finish();
                return true;
            case R.id.reply_with_message:
                broadcastLog("user chose to reply with message");
                //not sure if we need to do anything here, maybe just log the touch
                break;
            case R.id.busy:
                //indicate that they're replying
                broadcastLog("user declining with \"busy\" message");
                mSoundPool.stop(stopRingtoneID);
                return true;
            case R.id.call_you_back:
                //indicate that they're replying
                broadcastLog("user declining with \"call_you_back\" message");
                mSoundPool.stop(stopRingtoneID);
                return true;
            case R.id.lol:
                //indicate that they're replying
                broadcastLog("user declining with \"lol\" message");
                mSoundPool.stop(stopRingtoneID);
                return true;
            default:
                return true;

        }

        return super.onMenuItemSelected(featureId, item);
    }

    public void broadcastLog(String message){
        Intent logMessage = new Intent("log");
        logMessage.putExtra("message", message);
        getApplicationContext().sendBroadcast(logMessage);
    }
}
