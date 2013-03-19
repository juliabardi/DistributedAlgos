package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.activity.DataCollectService.ServiceBinder;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.receiver.SensorsListener.Sensors;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements
		OnSharedPreferenceChangeListener {
	
	private DataCollectService mService;	
	private Intent intent = null;
	private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                        
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
		
		intent = new Intent(this, DataCollectService.class);	
		
		//start service if not already started
		if (!isServiceRunning()){
			this.startService(intent);
		}
    }
	
	@Override
	protected void onStart() {		
		super.onStart();
		this.bindService(intent, mConnection, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
	}

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServiceBinder binder = (ServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

	public void onClick(View v){
		
		//navigate to the details
		Intent intent = new Intent(this, DetailsActivity.class);
		intent.putExtra("id", v.getId());
		this.startActivity(intent);		
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
				mService.registerSensorsListener(Sensors.ACCELEROMETER);
			} else {
				mService.unregisterSensorsListener(Sensors.ACCELEROMETER);
			}
		}
		else if (key.equals("light")){
			if (sharedPreferences.getBoolean("light", false)){
				mService.registerSensorsListener(Sensors.LIGHT);
			} else {
				mService.unregisterSensorsListener(Sensors.LIGHT);
			}
		}
		else if (key.equals("temperature")){
			if (sharedPreferences.getBoolean("temperature", false)){
				mService.registerSensorsListener(Sensors.TEMPERATURE);
			} else {
				mService.unregisterSensorsListener(Sensors.TEMPERATURE);
			}
		}
		else if (key.equals("location")){
			if (sharedPreferences.getBoolean("location", false)){
				mService.registerLocationListener();
			} else {
				mService.unregisterLocationListener();
			}
		}
		else if (key.equals("incoming")){
			if (sharedPreferences.getBoolean("incoming", false)){
				mService.registerReceiverIncoming();
			} else {
				mService.unregisterReceiverIncoming();
			}
		}
		else if (key.equals("outgoing")){
			if (sharedPreferences.getBoolean("outgoing", false)){
				mService.registerReceiverOutgoing();
			} else {
				mService.unregisterReceiverOutgoing();
			}
		}
	}
	
	private boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (DataCollectService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void stopClicked(View v){		
		stopService(intent);
	}
}
