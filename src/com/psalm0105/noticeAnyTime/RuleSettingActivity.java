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
        if (ruleType.getValue() != null) {
        	ruleType.setSummary(ruleType.getValue());
        }
        ruleType.setOnPreferenceChangeListener(new PreferenceChangeListener());
        
        EditTextPreference ruleFilter = (EditTextPreference) findPreference("RULE_FILTER");
        if (ruleFilter.getText() != null) {
        	ruleFilter.setSummary(ruleFilter.getText());
        }
        ruleFilter.setOnPreferenceChangeListener(new PreferenceChangeListener());
        
        EditTextPreference ruleAction = (EditTextPreference) findPreference("RULE_ACTION");
        if (ruleAction.getText() != null) {
        	ruleAction.setSummary(ruleAction.getText());
        }
        ruleAction.setOnPreferenceChangeListener(new PreferenceChangeListener());
        if (ruleType.getValue() != null) {
            if (ruleType.getValue().equals(res.getString(R.string.rule_setting_type_alarm))) {
            	ruleAction.setEnabled(false);
        	} else if (ruleType.getValue().equals(res.getString(R.string.rule_setting_type_call))) { 
        		ruleAction.setEnabled(true);
        	}

            setRuleActionSummary(ruleType.getValue(),ruleAction, ruleAction.getText());
        }
    }
	
	public void setRuleActionSummary(String ruleType, EditTextPreference actionPreference, String ruleAction) {
		Resources res = getResources();
	    if (ruleType != null && ruleType.equals(res.getString(R.string.rule_setting_type_call))) { 
			if (ruleAction != null && !"".equals(ruleAction)) {
				actionPreference.setSummary(ruleAction);
	        } else {
	        	actionPreference.setSummary(res.getString(R.string.rule_setting_action_desc_summary));
	        }
		} else if (ruleType == null || ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
	    	actionPreference.setSummary("");
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
	        Resources res = getResources();
			if ("RULE_TYPE".equals(preference.getKey())) {
				String ruleType = (String) newValue;

		        EditTextPreference ruleAction = (EditTextPreference) findPreference("RULE_ACTION");
		        		        
		        if (ruleType.equals(res.getString(R.string.rule_setting_type_alarm))) {
					ruleAction.setEnabled(false);
				} else if (ruleType.equals(res.getString(R.string.rule_setting_type_call))) {
					ruleAction.setEnabled(true);
				}
				preference.setSummary((CharSequence) ruleType);	
		        setRuleActionSummary(ruleType,ruleAction,ruleAction.getText());
			} else if ("RULE_ACTION".equals(preference.getKey())) {
		        ListPreference ruleType = (ListPreference) findPreference("RULE_TYPE");
		        setRuleActionSummary(ruleType.getValue(),(EditTextPreference) preference,(String) newValue);
		        
			} else {
				preference.setSummary((CharSequence) newValue);				
			}
						
			return true;
		}

	}
    
}