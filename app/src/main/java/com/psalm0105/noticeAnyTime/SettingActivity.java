package com.psalm0105.noticeAnyTime;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.psalm0105.noticeAnyTime.R;

public class SettingActivity extends PreferenceActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName("RULE");   
        addPreferencesFromResource(R.layout.preferences);
    }
    
}