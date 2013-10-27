package hu.bme.aut.datacollect.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FirstActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		if (((MyApp) getApplication()).isFirstRun()) {
			((MyApp) getApplication()).setRunned();

			this.setContentView(R.layout.first_view);
			
			
		} else {
			this.startActivity(new Intent(this, MainActivity.class));
		}
	}
	
	public void onSave(View view){
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DataCollectService.DEC_NODE_IP, 
				((TextView)findViewById(R.id.nodeAddress)).getText().toString());
		editor.putString(DataCollectService.DEC_NODE_PORT, 
				((TextView)findViewById(R.id.nodePort)).getText().toString());
		editor.putString(DataCollectService.DEC_ADMIN_IP, 
				((TextView)findViewById(R.id.gcmAddress)).getText().toString());
		editor.putString(DataCollectService.DEC_ADMIN_PORT, 
				((TextView)findViewById(R.id.gcmPort)).getText().toString());
		RadioGroup nodeProtocol = (RadioGroup)findViewById(R.id.radioNodeProtocol);
		String selectedNodeProtocol="http";
		switch (nodeProtocol.getCheckedRadioButtonId()) {
		case R.id.radioButtonNodeHTTPS:
			selectedNodeProtocol="https";
			break;
		default:
			break;
		}
		editor.putString(DataCollectService.DEC_NODE_PROTOCOL, selectedNodeProtocol);

		RadioGroup dataCollectorProtocol = (RadioGroup)findViewById(R.id.radioDataCollectorProtocol);
		String selectedDataCollectorProtocol="https";
		switch (dataCollectorProtocol.getCheckedRadioButtonId()) {
		case R.id.radioButtonDataCollectorHTTP:
			selectedNodeProtocol="http";
			break;
		default:
			break;
		}
		
		editor.putString(DataCollectService.DATA_COLLECTOR_PROTOCOL, selectedDataCollectorProtocol);
		
		editor.commit();
		
		this.startActivity(new Intent(this, MainActivity.class));
	}
	
	

}
