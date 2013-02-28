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
	        //Bundel 澄 眉农
		    Bundle bundle = intent.getExtras();
		    if (bundle == null) {
		    	return;
		    }
		
		    //pdu 按眉 澄 眉农
		    Object[] pdusObj = (Object[]) bundle.get("pdus");
		    if (pdusObj == null) {
		    	return;
		    }
		
		    //message 贸府
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
				originatingAddress = " ⑾"+smsMessages[i].getOriginatingAddress()+"";
				Log.d(logTag, "MessageBody : "
				                 + smsMessages[i].getMessageBody());
				messageBody.append(smsMessages[i].getMessageBody());
				Log.d(logTag, "ServiceCenterAddress : "
				                 + smsMessages[i].getServiceCenterAddress());
				Log.d(logTag, "TimestampMillis : "
		                                     + smsMessages[i].getTimestampMillis());
		    }	
			
        	SharedPreferences prefsRule = context.getSharedPreferences("RULE", Context.MODE_PRIVATE);  
        	if (prefsRule.getBoolean("SETTINGS_NOTICE_ENABLED", false)) {
    		    DatabaseAdapter databaseAdapter = new DatabaseAdapter(context).open();
    		    Cursor mCursor = databaseAdapter.getEnable();
    		    mCursor.moveToFirst();
    		    do {
    		    	int keyId = mCursor.getInt(mCursor.getColumnIndex(DatabaseAdapter.KEY_ID));
    		    	SharedPreferences prefsByKey = context.getSharedPreferences("RULE_"+keyId, Context.MODE_PRIVATE); 
    		    	String ruleType = prefsByKey.getString("RULE_TYPE", "");
    		    	String filter = prefsByKey.getString("RULE_FILTER", "");
    		    	String action = prefsByKey.getString("RULE_ACTION", "");
    		    	Log.d(logTag, "keyId : "+keyId + ",ruleType : "+ruleType + ",filter : "+filter + ",action : "+action);
    		    	Log.d(logTag, "messageBody indexOf : "+messageBody.toString().indexOf(filter));
    		    	if (!"".equals(ruleType) && !"".equals(filter) 
    		    			&& messageBody.toString().indexOf(filter) >= 0) {
    		    		// SmsMatchOk!
    		            Resources res = context.getResources();
    		            String notifyMessage = "";
    			    	if (ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
    				    	Log.d(logTag, "match Ok - alarm");
    			        	// play alarm
    				    	notifyMessage = filter + " > " + ruleType;
    			    	} else if (ruleType.equals(res.getString(R.string.rule_setting_type_call))) { 
    				    	Log.d(logTag, "match Ok - call");
    			    		// call
    				    	notifyMessage = filter + " > " + ruleType +" > "+ action;
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
			            notification.setLatestEventInfo(context, "Notice AnyTime", notifyMessage, contentIntent);
			            
			            notificationManager.cancel("NAT", 1);
			            notificationManager.notify("NAT", 1, notification);
    			    	
    			    	break;
    		    	}
    		    	
    		    } while (mCursor.moveToNext());
    		    
    		    mCursor.close();
    			databaseAdapter.close();		
    			
            	if (prefsRule.getBoolean("SETTINGS_TOAST_ENABLED", false)) {
        	        Toast.makeText(context, messageBody.toString()+originatingAddress, Toast.LENGTH_LONG).show();        		
            	}	
        	}
	    }
    }
}
