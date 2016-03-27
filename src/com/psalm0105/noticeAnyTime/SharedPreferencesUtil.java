package com.psalm0105.noticeAnyTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class SharedPreferencesUtil {
	private SharedPreferences prefs;
	private Context context;
	public SharedPreferencesUtil(Context context, String name) {
		this.context = context;
		this.prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}
	public String getMessageById() {
    	String ruleType = prefs.getString("RULE_TYPE", "");
    	String filter = prefs.getString("RULE_FILTER", "");
    	String action = prefs.getString("RULE_ACTION", "");
    	
        Resources res = context.getResources();
        String message = "";
    	if (ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
	    	message = "["+filter + "] " + ruleType;
    	} else if (ruleType.equals(res.getString(R.string.rule_setting_type_call))) { 
	    	message = "["+filter + "] " + ruleType +" : "+ action;
    	} else if (!"".equals(filter)) {
    		message = "["+filter + "]";
    	}
		return message;
	}
	
	public String getListEnableById() {
		boolean enable = prefs.getBoolean("RULE_ENABLED", false);
    	if (enable) {    		
    		return "[ON]";
    	} else {	
    		return "[OFF]";
    	}
	}
	
	public boolean getBooleanByKey(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}
	
	public String getStringByKey(String key, String defValue) {
		return prefs.getString(key, defValue);
	}
	
	public String getListMessageById() {
    	String ruleType = prefs.getString("RULE_TYPE", "");
    	String action = prefs.getString("RULE_ACTION", "");
    	
        String message = "";
        String preMessage = context.getResources().getString(R.string.rule_list_message_pre);
        if (!"".equals(action) && "Call".equals(ruleType)){
	    	message = preMessage + " : " + ruleType+" - "+action;
    	} else { 
	    	message = preMessage + " : " + ruleType;
    	}
		return message;
	}
}
