package com.psalm0105.noticeAnyTime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NoticeAnyTimeService extends Service {
	static final String logTag = "NoticeAnyTimeService";
    public static final String SERVICE_NAME = "com.psalm0105.noticeAnyTime.NoticeAnyTimeService";
    
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private final int STREAM_TYPE = AudioManager.STREAM_ALARM;
    
    private BroadcastReceiver smsReceiver;
    IBinder mBinder = new LocalBinder();

    
    public static Intent getIntent() {
        return new Intent(SERVICE_NAME);
    }
 
    @Override
    public void onCreate() {
    	Log.d(logTag, "NoticeAnyTimeService.onCreate()");
    	smsReceiver = new SmsReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("android.provider.Telephony.SMS_RECEIVED");
    	registerReceiver(smsReceiver, filter);  
    	mediaPlayer = new MediaPlayer(); 
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }    
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(logTag, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(logTag, "onDestroy()...");
        unregisterReceiver(smsReceiver);
        mediaPlayer.release();
    }

    
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public void playAlarm() {
        Log.d(logTag, "playAlarm()...");	
        
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    	SharedPreferences prefsRule = getSharedPreferences("RULE", Context.MODE_PRIVATE); 
    	String mediaPath = prefsRule.getString("SETTINGS_RINGTONE",alarmUri.getPath());
        try {
        	mediaPlayer.setDataSource(this, Uri.parse(mediaPath));

        	audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        	audioManager.setStreamVolume(STREAM_TYPE, audioManager.getStreamMaxVolume(STREAM_TYPE), AudioManager.FLAG_PLAY_SOUND);
        	
        	mediaPlayer.setAudioStreamType(STREAM_TYPE);
        	mediaPlayer.prepare();
        	mediaPlayer.setLooping(true); 
        	mediaPlayer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopAlarm() {
        Log.d(logTag, "stopAlarm()...");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();	
        }
	}
	
    public class LocalBinder extends Binder {
        public NoticeAnyTimeService getServerInstance() {
            return NoticeAnyTimeService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){    
        Log.d(logTag, "onStartCommand()...");		
    	if (intent != null && intent.getBooleanExtra("StartAlarm", false)) {
    		playAlarm();
    	}
        return START_STICKY;
    }
}
