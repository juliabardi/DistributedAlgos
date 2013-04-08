package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;
import hu.bme.aut.datacollect.activity.DataCollectService.ServiceBinder;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.receiver.IListener;
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
	private CommunicationService commService;
	private Intent intent = null;
	private Intent commIntent=null;
	private boolean mBound = false;
	private boolean commBound = false;
	
	public static final String[] sharedPrefKeys = new String[] { "acceleration", "light",
			"temperature", "location", "incall", "outcall", "insms", "outsms" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                        
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
		
		intent = new Intent(this, DataCollectService.class);
		commIntent = new Intent(this,CommunicationService.class); 
		
		//start service if not already started
		if (!isServiceRunning(DataCollectService.class.getName())){
			this.startService(intent);
		}
		if (!isServiceRunning(CommunicationService.class.getName())){
			this.startService(commIntent);
		}
    }
	
	@Override
	protected void onStart() {		
		super.onStart();
		this.bindService(intent, mConnection, 0);
		this.bindService(commIntent, commConnection, 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (commBound) {
            unbindService(commConnection);
            commBound = false;
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
    
    private ServiceConnection commConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommServiceBinder binder = (CommServiceBinder) service;
            commService = binder.getService();
            commBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            commBound = false;
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
		
		//finding which shared preference was changed, and register/unregister according to the value
		for (String sharedKey : sharedPrefKeys){
			if (sharedKey.equals(key)) {
				IListener listener = mService.getListener(sharedKey);
				if (sharedPreferences.getBoolean(sharedKey, false))
					listener.register();
				else
					listener.unregister();
				break;
			}
		}
	}
	
	private boolean isServiceRunning(String serviceName) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void stopClicked(View v){		
		stopService(intent);
		stopService(commIntent);
	}
}
