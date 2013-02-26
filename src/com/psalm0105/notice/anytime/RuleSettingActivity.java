package com.psalm0105.notice.anytime;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.psalm0105.notice.anytime.R;

public class RuleSettingActivity extends PreferenceActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        getPreferenceManager().setSharedPreferencesName("RULE_"+id);        
        addPreferencesFromResource(R.layout.rulepreferences);
    }
    
}