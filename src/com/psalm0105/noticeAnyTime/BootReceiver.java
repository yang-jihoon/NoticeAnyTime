package com.psalm0105.noticeAnyTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
    private Context cont;
	static final String logTag = "BootReceiver";
    
    @Override
	public void onReceive(Context context, Intent intent) {
    	if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
    		Log.d(logTag, "BootReceiver.onReceive()...");
    		this.cont = context;
   
    		Thread serviceThread = new Thread(new Runnable() {
                public void run() {
                    Intent intent = NoticeAnyTimeService.getIntent();
                    cont.startService(intent);
                }
            });
            serviceThread.start();   
    	}
    }
}

