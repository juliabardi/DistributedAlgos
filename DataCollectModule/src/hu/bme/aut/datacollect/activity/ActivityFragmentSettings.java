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
	final static String ACTION_PREFS_SERVERS = "hu.bme.aut.datacollect.prefs.PREFS_SERVERS";
	
	static Map<String, Boolean> availableListeners = new HashMap<String, Boolean>();
	
	//used by LocationProvider to show alert dialog if GPS is not enabled
	//instance is not null between onCreate and onDestroy
	public static ActivityFragmentSettings instance = null;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActivityFragmentSettings.instance = this;
		
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
            if (!availableListeners.get(DataCollectService.PROXIMITY)){
            	this.findPreference(DataCollectService.PROXIMITY).setEnabled(false);
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
	        if (!availableListeners.get(DataCollectService.CALL)){
	        	this.findPreference(DataCollectService.CALL).setEnabled(false);
	        	this.findPreference(DataCollectService.SMS).setEnabled(false);
	        } 
	    }
	    else if (action != null && action.equals(ACTION_PREFS_SERVERS)){
	    	addPreferencesFromResource(R.xml.serversettings);
	    }
	    else {
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
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityFragmentSettings.instance = null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	@Override
	public void onBuildHeaders(List<Header> target) {
		//giving up on fragments, because cannot mix headers with normal preferences
		//loadHeadersFromResource(R.xml.fragmentsettings, target);
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
            if (!availableListeners.get(DataCollectService.PROXIMITY)){
            	this.findPreference(DataCollectService.PROXIMITY).setEnabled(false);
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
            if (!availableListeners.get(DataCollectService.CALL)){
            	this.findPreference(DataCollectService.CALL).setEnabled(false);
            	this.findPreference(DataCollectService.SMS).setEnabled(false);
            }
        }
    }
}
