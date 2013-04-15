package hu.bme.aut.datacollect.activity;

import java.util.List;

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
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//handling sdk pre11
	    String action = getIntent().getAction();
	    if (action != null && action.equals(ACTION_PREFS_SENSORS)) {
	        addPreferencesFromResource(R.xml.sensorsettings);
	    }
	    else if (action != null && action.equals(ACTION_PREFS_LOCATION)) {
	        addPreferencesFromResource(R.xml.locsettings);
	    }
	    else if (action != null && action.equals(ACTION_PREFS_CALLS)) {
	        addPreferencesFromResource(R.xml.callsettings);
	    }
	    else if (action != null && action.equals(ACTION_PREFS_SMS)) {
	        addPreferencesFromResource(R.xml.smssettings);
	    }
		
	    else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        // Load the legacy preferences headers
	        addPreferencesFromResource(R.xml.preference_headers_legacy);
	    }	
	    
/*		if (hasHeaders()) {
			Button button = new Button(this);
			button.setText("Mentés");
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
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsLoc extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.locsettings);
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsCalls extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.callsettings);
        }
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    public static class FragmentSettingsSms extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.smssettings);
        }
    }
}
