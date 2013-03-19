package hu.bme.aut.datacollect.activity;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityFragmentSettings extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (hasHeaders()) {
			Button button = new Button(this);
			button.setText("Mentés");
			setListFooter(button);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
	}
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.fragmentsettings, target);
	}
    public static class FragmentSettingsSensors extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.sensorsettings);
        }
    }
    public static class FragmentSettingsLoc extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.locsettings);
        }
    }
    public static class FragmentSettingsCalls extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.callsettings);
        }
    }
}
