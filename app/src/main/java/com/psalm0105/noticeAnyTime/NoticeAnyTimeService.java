package com.psalm0105.noticeAnyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NoticeAnyTimeService extends Service {
	static final String logTag = "NoticeAnyTimeService";
    public static final String SERVICE_NAME = "com.psalm0105.noticeAnyTime.NoticeAnyTimeService";
    
    private static List<MediaPlayer> mediaPlayers = new ArrayList<MediaPlayer>();

    private AudioManager audioManager;
    private final int STREAM_TYPE = AudioManager.STREAM_ALARM;
    
    private int playCount = 0;
    
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
    	audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    	
    	Timer snoozeCancelTimer = new Timer();
		snoozeCancelTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Log.d("SNOOZE", "Run snooze cacel timer");

				SharedPreferences snoozePref = getSharedPreferences("SNOOZE_TIME", Context.MODE_PRIVATE);
				int snoozeType = snoozePref.getInt("TYPE", 0);
				long snoozeTime = snoozePref.getLong("TIME", 0);

				Date currentTime = new Date();
				if (snoozeType > 0 && snoozeTime <= currentTime.getTime()) {
					Log.d("SNOOZE", "Snooze notification cacel !!");

					NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel("NAT", NOTICE_TYPE.SNOOZE.getTypeNum());

					SharedPreferences prefs = getSharedPreferences("SNOOZE_TIME", Context.MODE_PRIVATE);
					Editor ed = prefs.edit();
					ed.putInt("TYPE", 0);
					ed.commit();
				}

			}

		}, 0, 1000 * 60);
    }    
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(logTag, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(logTag, "onDestroy()...");
        unregisterReceiver(smsReceiver);
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			mediaPlayer.release();
		}
    }

    
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	public void playAlarm() {
        Log.d(logTag, "playAlarm()...[" + mediaPlayers.size()+"]");
        
    	SharedPreferences prefsRule = getSharedPreferences("RULE", Context.MODE_PRIVATE);
    	String settingRingtone = prefsRule.getString("SETTINGS_RINGTONE","");
    	Uri uri;
    	if ("".equals(settingRingtone)){
    		uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    	} else {
    		uri = Uri.parse(settingRingtone);
    	}

        try {
        	playCount = 0;
			MediaPlayer mediaPlayer = new MediaPlayer();
        	mediaPlayer.setDataSource(this, uri);
        	
        	mediaPlayer.setAudioStreamType(STREAM_TYPE);
        	mediaPlayer.prepare();
        	mediaPlayer.setLooping(false);
        	mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer player) {
					playCount += 1;
					Log.d(logTag, "PlayCount : " + playCount);
					if (playCount <= 2) {
						player.start();
					} else {
						stopAlarm();
					}

				}
			});
        	mediaPlayer.start();

			mediaPlayers.add(mediaPlayer);

        	audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        	audioManager.setStreamVolume(STREAM_TYPE, audioManager.getStreamMaxVolume(STREAM_TYPE), AudioManager.FLAG_PLAY_SOUND);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopAlarm() {
        Log.d(logTag, "stopAlarm()...");
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			mediaPlayer.stop();
		}
		mediaPlayers.clear();
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
    
    public boolean isAlarmPlaying() {
        if (mediaPlayers.size() > 0) {
            return true;
        } else {
        	return false;
        }
    }
}
