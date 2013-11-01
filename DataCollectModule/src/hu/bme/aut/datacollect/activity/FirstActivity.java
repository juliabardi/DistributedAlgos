package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FirstActivity extends Activity {
	
	final static String ACTION_PREFS_SERVERS = "hu.bme.aut.datacollect.prefs.PREFS_SERVERS";
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		if (((MyApp) getApplication()).isFirstRun()) {
			((MyApp) getApplication()).setRunned();

			this.setContentView(R.layout.first_view);
		}
		else if (getIntent()!=null && ACTION_PREFS_SERVERS.equals(getIntent().getAction())){
			
			this.setContentView(R.layout.first_view);
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
			((EditText)findViewById(R.id.nodeAddress)).setText(settings.getString(DataCollectService.DEC_NODE_IP, Constants.NodeServerIP));
			((EditText)findViewById(R.id.nodePortHTTP)).setText(settings.getString(DataCollectService.DEC_NODE_PORT, Constants.NodeServerPort));
			((EditText)findViewById(R.id.nodePortHTTPS)).setText(settings.getString(DataCollectService.DEC_NODE_PORT_HTTPS, Constants.NodeServerPortHttps));
			
			//default http
			if (!Constants.NodeServerProtocol.equals(settings.getString(DataCollectService.DEC_NODE_PROTOCOL, Constants.NodeServerProtocol))){
				((RadioButton)findViewById(R.id.radioButtonNodeHTTPS)).setChecked(true);
			}

			((EditText)findViewById(R.id.gcmAddress)).setText(settings.getString(DataCollectService.DEC_ADMIN_IP, Constants.GCMServerIP));
			((EditText)findViewById(R.id.gcmPortHTTP)).setText(settings.getString(DataCollectService.DEC_ADMIN_PORT, Constants.GCMServerPort));
			((EditText)findViewById(R.id.gcmPortHTTPS)).setText(settings.getString(DataCollectService.DEC_ADMIN_PORT_HTTPS, Constants.GCMServerPortHttps));
			
			//default http
			if (!Constants.GCMServerProtocol.equals(settings.getString(DataCollectService.DEC_ADMIN_PROTOCOL, Constants.GCMServerProtocol))){
				((RadioButton)findViewById(R.id.radioButtonGCMHTTPS)).setChecked(true);
			}
			
			((EditText)findViewById(R.id.dataCollectorAddress)).setText(settings.getString(DataCollectService.DATA_COLLECTOR_IP, Constants.DataCollectorServerIP));
			((EditText)findViewById(R.id.dataCollectorPortHTTP)).setText(settings.getString(DataCollectService.DATA_COLLECTOR_PORT, Constants.DataCollectorServerPort));
			((EditText)findViewById(R.id.dataCollectorPortHTTPS)).setText(settings.getString(DataCollectService.DATA_COLLECTOR_PORT_HTTPS, Constants.DataCollectorServerPortHttps));			
			
			//default https
			if (!Constants.DataCollectorServerProtocol.equals(settings.getString(DataCollectService.DATA_COLLECTOR_PROTOCOL, Constants.DataCollectorServerProtocol))){
				((RadioButton)findViewById(R.id.radioButtonDataCollectorHTTP)).setChecked(true);
			}
			
		} else {
			this.startActivity(new Intent(this, MainActivity.class));
		}
	}
	
	public void changeProtocol(View view){
		switch (view.getId()) {
		case R.id.radioButtonNodeHTTP:
			setupButton(R.id.radioButtonNodeHTTP, R.id.radioButtonNodeHTTPS);
			break;
		case R.id.radioButtonNodeHTTPS:
			setupButton(R.id.radioButtonNodeHTTPS, R.id.radioButtonNodeHTTP);
			break;
		case R.id.radioButtonGCMHTTP:
			setupButton(R.id.radioButtonGCMHTTP, R.id.radioButtonGCMHTTPS);
			break;
		case R.id.radioButtonGCMHTTPS:
			setupButton(R.id.radioButtonGCMHTTPS, R.id.radioButtonGCMHTTP);
			break;
		case R.id.radioButtonDataCollectorHTTP:
			setupButton(R.id.radioButtonDataCollectorHTTP, R.id.radioButtonDataCollectorHTTPS);
			break;
		case R.id.radioButtonDataCollectorHTTPS:
			setupButton(R.id.radioButtonDataCollectorHTTPS, R.id.radioButtonDataCollectorHTTP);
			break;
		default:
			break;
		}
	}
	
	private void setupButton(int buttonClick,int buttonStatePartner ){
		if(((RadioButton)findViewById(buttonClick)).isChecked()){
			((RadioButton)findViewById(buttonStatePartner)).setChecked(false);
		}else{
			((RadioButton)findViewById(buttonStatePartner)).setChecked(true);
		}
	}
	
	public void onSave(View view){
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DataCollectService.DEC_NODE_IP, 
				((TextView)findViewById(R.id.nodeAddress)).getText().toString());
		editor.putString(DataCollectService.DEC_NODE_PORT, 
				((TextView)findViewById(R.id.nodePortHTTP)).getText().toString());
		editor.putString(DataCollectService.DEC_NODE_PORT_HTTPS, 
				((TextView)findViewById(R.id.nodePortHTTPS)).getText().toString());
		editor.putString(DataCollectService.DEC_ADMIN_IP, 
				((TextView)findViewById(R.id.gcmAddress)).getText().toString());
		editor.putString(DataCollectService.DEC_ADMIN_PORT, 
				((TextView)findViewById(R.id.gcmPortHTTP)).getText().toString());
		editor.putString(DataCollectService.DEC_ADMIN_PORT_HTTPS, 
				((TextView)findViewById(R.id.gcmPortHTTPS)).getText().toString());
		editor.putString(DataCollectService.DATA_COLLECTOR_IP, 
				((TextView)findViewById(R.id.dataCollectorAddress)).getText().toString());
		editor.putString(DataCollectService.DATA_COLLECTOR_PORT, 
				((TextView)findViewById(R.id.dataCollectorPortHTTP)).getText().toString());
		editor.putString(DataCollectService.DATA_COLLECTOR_PORT_HTTPS, 
				((TextView)findViewById(R.id.dataCollectorPortHTTPS)).getText().toString());
	
		editor.putString(DataCollectService.DEC_NODE_PROTOCOL, ((RadioButton)findViewById(R.id.radioButtonNodeHTTP)).isChecked()? Constants.HTTP:Constants.HTTPS);
		editor.putString(DataCollectService.DEC_ADMIN_PROTOCOL, ((RadioButton)findViewById(R.id.radioButtonGCMHTTP)).isChecked()? Constants.HTTP:Constants.HTTPS);
		editor.putString(DataCollectService.DATA_COLLECTOR_PROTOCOL, ((RadioButton)findViewById(R.id.radioButtonDataCollectorHTTP)).isChecked()? Constants.HTTP:Constants.HTTPS);
		
		editor.commit();
		
		this.startActivity(new Intent(this, MainActivity.class));
	}
	
	

}
