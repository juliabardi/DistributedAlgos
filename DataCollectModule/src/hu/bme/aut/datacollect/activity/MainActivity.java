package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.receiver.IncomingCallReceiver;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.OutgoingCallReceiver;
import hu.bme.aut.datacollect.receiver.SensorsListener;
import hu.bme.aut.datacollect.receiver.SensorsListener.Sensors;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements
		OnSharedPreferenceChangeListener {
	
	private LocationProvider locProvider;
	private SensorsListener sensorsListener;
	private IncomingCallReceiver incomingReceiver;
	private OutgoingCallReceiver outgoingReceiver;
	
	boolean regIncoming = false;
	boolean regOutgoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        //instantiate class, it will register for loc updates
        locProvider = new LocationProvider(this, getHelper().getLocationDao());
        
        //instantiate to register
		sensorsListener = new SensorsListener(this, getHelper()
				.getAccelerationDao(), getHelper().getLightDao(), getHelper()
				.getTemperatureDao());
 
        incomingReceiver = new IncomingCallReceiver(getHelper().getCallDao());
        outgoingReceiver = new OutgoingCallReceiver(getHelper().getCallDao());
        
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
    }

	@Override
	protected void onDestroy() {
		
		//unregister stuff if necessary
		locProvider.unregisterListener();
		sensorsListener.unregisterListener();
		this.unregisterReceiverIncoming();
		this.unregisterReceiverOutgoing();
		
		super.onDestroy();
	}


	@Override
	protected void onPause() {
				
		super.onPause();
	}
    
	public void onClick(View v){
		
		//navigate to the details
		Intent intent = new Intent(this, DetailsActivity.class);
		intent.putExtra("id", v.getId());
		this.startActivity(intent);		
	}
	
	public void registerReceiverIncoming(){
		
		if (!regIncoming){
			IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
			this.registerReceiver(incomingReceiver, intentFilter);
			regIncoming = true;
		}
	}
	
	public void unregisterReceiverIncoming(){
		
		if (regIncoming){
			this.unregisterReceiver(incomingReceiver);
			regIncoming = false;
		}
	}
	
	public void registerReceiverOutgoing(){
		
		if (!regOutgoing){
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
			this.registerReceiver(outgoingReceiver, intentFilter);
			regOutgoing = true;
		}
	}
	
	public void unregisterReceiverOutgoing(){
		
		if (regOutgoing){
			this.unregisterReceiver(outgoingReceiver);
			regOutgoing = false;
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			Intent i = new Intent();
			i.setClass(this, ActivityFragmentSettings.class);
			startActivityForResult(i, 0);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//handle the preferences changes
		if (key.equals("acceleration")){
			if (sharedPreferences.getBoolean("acceleration", false)){
				sensorsListener.registerListener(Sensors.ACCELEROMETER);
			} else {
				sensorsListener.unregisterListener(Sensors.ACCELEROMETER);
			}
		}
		else if (key.equals("light")){
			if (sharedPreferences.getBoolean("light", false)){
				sensorsListener.registerListener(Sensors.LIGHT);
			} else {
				sensorsListener.unregisterListener(Sensors.LIGHT);
			}
		}
		else if (key.equals("temperature")){
			if (sharedPreferences.getBoolean("temperature", false)){
				sensorsListener.registerListener(Sensors.TEMPERATURE);
			} else {
				sensorsListener.unregisterListener(Sensors.TEMPERATURE);
			}
		}
		else if (key.equals("location")){
			if (sharedPreferences.getBoolean("location", false)){
				locProvider.registerListener();
			} else {
				locProvider.unregisterListener();
			}
		}
		else if (key.equals("incoming")){
			if (sharedPreferences.getBoolean("incoming", false)){
				this.registerReceiverIncoming();
			} else {
				this.unregisterReceiverIncoming();
			}
		}
		else if (key.equals("outgoing")){
			if (sharedPreferences.getBoolean("outgoing", false)){
				this.registerReceiverOutgoing();
			} else {
				this.unregisterReceiverOutgoing();
			}
		}
	}
}
