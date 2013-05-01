package hu.bme.aut.datacollect.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class ActivityFragmentSettings extends PreferenceActivity {
	
	final static String ACTION_PREFS_SENSORS = "hu.bme.aut.datacollect.prefs.PREFS_SENSORS";
	final static String ACTION_PREFS_LOCATION = "hu.bme.aut.datacollect.prefs.PREFS_LOCATION";
	final static String ACTION_PREFS_CALLS = "hu.bme.aut.datacollect.prefs.PREFS_CALLS";
	final static String ACTION_PREFS_SMS = "hu.bme.aut.datacollect.prefs.PREFS_SMS";
	final static String ACTION_PREFS_PACKAGE = "hu.bme.aut.datacollect.prefs.PREFS_PACKAGE";
	
	static Map<String, Boolean> availableListeners = new HashMap<String, Boolean>();
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//handling sdk pre11
	    String action = getIntent().getAction();
	    if (action != null && action.equals(ACTION_PREFS_SENSORS)) {
	        addPreferencesFromResource(R.xml.sensorsettings);
	        if (!availableListeners.get(DataCollectService.ACCELERATION)){
	        	this.findPreference(DataCollectService.ACCELERATION).setEnabled(false);
	        } 
	        if (!availableListeners.get(DataCollectService.LIGHT)){
	        	this.findPreference(DataCollectService.LIGHT).setEnabled(false);
	        } 
	        if (!availableListeners.get(DataCollectService.TEMPERATURE)){
	        	this.findPreference(DataCollectService.TEMPERATURE).setEnabled(false);
	        } 	
	        if (!availableListeners.get(DataCollectService.GYROSCOPE)){
	        	this.findPreference(DataCollectService.GYROSCOPE).setEnabled(false);
	        } 
	    }
	    else if (action != null && action.equals(ACTION_PREFS_LOCATION)) {
	        addPreferencesFromResource(R.xml.locsettings);
	        if (!availableListeners.get(DataCollectService.LOCATION)){
	        	this.findPreference(DataCollectService.LOCATION).setEnabled(false);
	        }
	    }
	    else if (action != null && action.equals(ACTION_PREFS_CALLS)) {
	        addPreferencesFromResource(R.xml.callsettings);
	        if (!availableListeners.get(DataCollectService.INCOMING_CALL)){
	        	this.findPreference(DataCollectService.INCOMING_CALL).setEnabled(false);
	        	this.findPreference(DataCollectService.OUTGOING_CALL).setEnabled(false);
	        } 
	    }
	    else if (action != null && action.equals(ACTION_PREFS_SMS)) {
	        addPreferencesFromResource(R.xml.smssettings);
	        if (!availableListeners.get(DataCollectService.INCOMING_SMS)){
	        	this.findPreference(DataCollectService.INCOMING_SMS).setEnabled(false);
	        	this.findPreference(DataCollectService.OUTGOING_SMS).setEnabled(false);
	        } 
	    }
	    else if (action != null && action.equals(ACTION_PREFS_PACKAGE)){
	    	addPreferencesFromResource(R.xml.packagesettings);	 
	    	//this is always enabled
	    }
		
	    else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        // Load the legacy preferences headers
	        addPreferencesFromResource(R.xml.preference_headers_legacy);
	    }	
	    
	    Bundle bundle = this.getIntent().getExtras();
	    if (bundle != null){
	    	//storing the available statuses
	    	for(String key : DataCollectService.sharedPrefKeys){
	    		if (bundle.containsKey(key)){
	    			this.availableListeners.put(key, bundle.getBoolean(key));
	    		}
	    	}
	    }
	    
/*		if (hasHeaders()) {
			Button button = new Button(this);
			button.setText("Ment�s");
			setListFooter(button);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
		*/
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.fragmentsettings, target);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsSensors extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.sensorsettings); 
            if (!availableListeners.get(DataCollectService.ACCELERATION)){
            	this.findPreference(DataCollectService.ACCELERATION).setEnabled(false);
            }
            if (!availableListeners.get(DataCollectService.LIGHT)){
            	this.findPreference(DataCollectService.LIGHT).setEnabled(false);
            }
            if (!availableListeners.get(DataCollectService.TEMPERATURE)){
            	this.findPreference(DataCollectService.TEMPERATURE).setEnabled(false);
            }
            if (!availableListeners.get(DataCollectService.GYROSCOPE)){
            	this.findPreference(DataCollectService.GYROSCOPE).setEnabled(false);
            }
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsLoc extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.locsettings);
            if (!availableListeners.get(DataCollectService.LOCATION)){
            	this.findPreference(DataCollectService.LOCATION).setEnabled(false);
            }
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsCalls extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.callsettings);
            if (!availableListeners.get(DataCollectService.INCOMING_CALL)){
            	this.findPreference(DataCollectService.INCOMING_CALL).setEnabled(false);
            	this.findPreference(DataCollectService.OUTGOING_CALL).setEnabled(false);
            }
        }
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsSms extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.smssettings);
            if (!availableListeners.get(DataCollectService.INCOMING_SMS)){
            	this.findPreference(DataCollectService.INCOMING_SMS).setEnabled(false);
            	this.findPreference(DataCollectService.OUTGOING_SMS).setEnabled(false);
            }
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsPackage extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.packagesettings);
        }
    }
}
