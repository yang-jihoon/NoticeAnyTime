package com.psalm0105.noticeAnyTime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class NoticeAnyTimeService extends Service {
	static final String logTag = "NoticeAnyTimeService";
    public static final String SERVICE_NAME = "com.psalm0105.noticeAnyTime.NoticeAnyTimeService";
    
    private BroadcastReceiver smsReceiver;
    
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
    }    
    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(logTag, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(logTag, "onDestroy()...");
        unregisterReceiver(smsReceiver);
    }

    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
