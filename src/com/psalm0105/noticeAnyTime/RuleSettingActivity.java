package com.psalm0105.noticeAnyTime;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class RuleSettingActivity extends PreferenceActivity {

	private DatabaseAdapter databaseAdapter;
	static final String logTag = "RuleSettingActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);	

        databaseAdapter = new DatabaseAdapter(this).open();

        Resources res = getResources();
        Intent intent = getIntent();        
        String id = intent.getStringExtra("id");
        
        if ("0".equals(id)) {		            
			//Insert new
        	RuleDomain ruleDomain = new RuleDomain();
			ruleDomain.setEnable("true");
        	id = String.valueOf(databaseAdapter.insert(ruleDomain)); 
            intent.putExtra("id", id);            
        }
        getPreferenceManager().setSharedPreferencesName("RULE_"+id);

        addPreferencesFromResource(R.layout.rulepreferences);
         
        
        CheckBoxPreference enable = (CheckBoxPreference) findPreference("RULE_ENABLED");
        enable.setOnPreferenceClickListener(new PreferenceClickListener(id));
                
        ListPreference ruleType = (ListPreference) findPreference("RULE_TYPE");
        ruleType.setSummary(ruleType.getEntry());
        ruleType.setOnPreferenceChangeListener(new PreferenceChangeListener());
        
        EditTextPreference ruleFilter = (EditTextPreference) findPreference("RULE_FILTER");
        ruleFilter.setSummary(ruleFilter.getText());
        ruleFilter.setOnPreferenceChangeListener(new PreferenceChangeListener());
        
        EditTextPreference ruleAction = (EditTextPreference) findPreference("RULE_ACTION");
        ruleAction.setSummary(ruleAction.getText());
        ruleAction.setOnPreferenceChangeListener(new PreferenceChangeListener());
        if (ruleType.getValue() != null) {
            if (ruleType.getValue().equals(res.getString(R.string.rule_setting_type_alarm))) {
            	ruleAction.setEnabled(false);
				ruleAction.setText("");
        	} else if (ruleType.getValue().equals(res.getString(R.string.rule_setting_type_call))) { 
        		ruleAction.setEnabled(true);
        	}	
        }
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		databaseAdapter.close();
	}

	public class PreferenceClickListener implements OnPreferenceClickListener {
		
		private String id = "0";
		
		public PreferenceClickListener(String id){
			this.id = id;
		}

		public boolean onPreferenceClick(Preference preference) {
			CheckBoxPreference enable = (CheckBoxPreference)preference;
			RuleDomain ruleDomain = new RuleDomain();
			ruleDomain.setId(Integer.parseInt(id));
			if (enable.isChecked()) {
				ruleDomain.setEnable("true");
			} else {
				ruleDomain.setEnable("false");				
			}
			
			databaseAdapter.update(ruleDomain);
			return true;
		}

	}
    
    public class PreferenceChangeListener implements OnPreferenceChangeListener {
    	
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if ("RULE_TYPE".equals(preference.getKey())) {
				String ruleType = (String) newValue;
				preference.setSummary((CharSequence) ruleType);	

		        EditTextPreference ruleAction = (EditTextPreference) findPreference("RULE_ACTION");
		        Resources res = getResources();
		        if (ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
					ruleAction.setEnabled(false);
					ruleAction.setText("");
					ruleAction.setSummary("");
				} else if (ruleType.equals(res.getString(R.string.rule_setting_type_call))) {
					ruleAction.setEnabled(true);
				}
			} else {
				preference.setSummary((CharSequence) newValue);				
			}
						
			return true;
		}

	}
    
}