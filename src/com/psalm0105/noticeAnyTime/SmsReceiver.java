package com.psalm0105.noticeAnyTime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	static final String logTag = "SmsReceiver";
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    @Override
    public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(ACTION)) {
	        //Bundel
		    Bundle bundle = intent.getExtras();
		    if (bundle == null) {
		    	return;
		    }
		
		    //pdu
		    Object[] pdusObj = (Object[]) bundle.get("pdus");
		    if (pdusObj == null) {
		    	return;
		    }
		
		    //message
		    SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];
		    StringBuilder messageBody = new StringBuilder();
		    String originatingAddress = "";
		    for (int i = 0; i < pdusObj.length; i++) {
		        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
		        Log.d(logTag, "NEW SMS " + i + "th");
				Log.d(logTag, "DisplayOriginatingAddress : "
				                 + smsMessages[i].getDisplayOriginatingAddress());
				Log.d(logTag, "DisplayMessageBody : "
				                 + smsMessages[i].getDisplayMessageBody());
				Log.d(logTag, "EmailBody : "
				                 + smsMessages[i].getEmailBody());
				Log.d(logTag, "EmailFrom : "
				                 + smsMessages[i].getEmailFrom());
				Log.d(logTag, "OriginatingAddress : "
				                 + smsMessages[i].getOriginatingAddress());
				originatingAddress = " ☎"+smsMessages[i].getOriginatingAddress()+"";
				Log.d(logTag, "MessageBody : "
				                 + smsMessages[i].getMessageBody());
				messageBody.append(smsMessages[i].getMessageBody());
				Log.d(logTag, "ServiceCenterAddress : "
				                 + smsMessages[i].getServiceCenterAddress());
				Log.d(logTag, "TimestampMillis : "
		                                     + smsMessages[i].getTimestampMillis());
		    }	
			
        	SharedPreferences prefsRule = context.getSharedPreferences("RULE", Context.MODE_PRIVATE);  
        	if (prefsRule.getBoolean("SETTINGS_NOTICE_ENABLED", true)) {
    		    DatabaseAdapter databaseAdapter = new DatabaseAdapter(context).open();
    		    Cursor mCursor = databaseAdapter.getEnable();
    		    mCursor.moveToFirst();
    		    do {
    		    	int keyId = mCursor.getInt(mCursor.getColumnIndex(DatabaseAdapter.KEY_ID));

    		    	SharedPreferencesUtil spUtilByID = new SharedPreferencesUtil(context,"RULE_"+keyId);    
    		    	String ruleType = spUtilByID.getStringByKey("RULE_TYPE", "");
    		    	String filter = spUtilByID.getStringByKey("RULE_FILTER", "");
    		    	String action = spUtilByID.getStringByKey("RULE_ACTION", "");

    		    	Log.d(logTag, filter + " - messageBody indexOf : "+messageBody.toString().indexOf(filter));
    		    	if (!"".equals(ruleType) && !"".equals(filter) 
    		    			&& messageBody.toString().indexOf(filter) >= 0) {
    		    		// SmsMatchOk!
    		            Resources res = context.getResources();
    			    	if (ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
    			        	// play alarm
    				    	Log.d(logTag, "match Ok - alarm");
    				    	
    				    	Intent serviceIntent = new Intent(NoticeAnyTimeService.SERVICE_NAME);  
    				    	serviceIntent.putExtra("StartAlarm", true);
    				    	context.startService(serviceIntent);			    	    				    	
    			    	} else if (ruleType.equals(res.getString(R.string.rule_setting_type_call))) { 
    			    		// call
    				    	Log.d(logTag, "match Ok - call");
    				    	if ("".equals(action)) {
    				    		action = originatingAddress; 				    		
    				    	}
    				    	Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ action));   
				    		callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    				    	context.startActivity(callIntent);	

    			    	}
    			    	
			    		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			            Notification notification = new Notification();   
			            notification.icon = R.drawable.ic_launcher;
			            notification.defaults = Notification.DEFAULT_LIGHTS;
			            notification.flags |= Notification.FLAG_NO_CLEAR;
			            notification.flags |= Notification.FLAG_ONGOING_EVENT;
			            notification.when = System.currentTimeMillis();
			            notification.tickerText = "Notice AnyTime"; 
			            
			            Intent intentForNotify = new Intent(context, RuleListActivity.class);
			            intentForNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            intentForNotify.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			            intentForNotify.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			            intentForNotify.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			            intentForNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentForNotify, PendingIntent.FLAG_UPDATE_CURRENT);
			            notification.setLatestEventInfo(context, "Notice AnyTime", spUtilByID.getMessageById(), contentIntent);
			            
			            notificationManager.cancel("NAT", 1);
			            notificationManager.notify("NAT", 1, notification);
    			    	
    			    	break;
    		    	}
    		    	
    		    } while (mCursor.moveToNext());
    		    
    		    mCursor.close();
    			databaseAdapter.close();		
    			
            	if (prefsRule.getBoolean("SETTINGS_TOAST_ENABLED", true)) {
        	        Toast.makeText(context, messageBody.toString()+originatingAddress, Toast.LENGTH_LONG).show();        		
            	}	
        	}
	    }
    }
    
    
}
