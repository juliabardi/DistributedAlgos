package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.CommunicationService;
import hu.bme.aut.communication.CommunicationService.CommServiceBinder;
import hu.bme.aut.datacollect.activity.DataCollectService.ServiceBinder;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.listener.IListener;
import hu.bme.aut.datacollect.listener.LocationProvider;
import hu.bme.aut.datacollect.upload.DataUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;
import hu.bme.aut.datacollect.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> implements
		OnSharedPreferenceChangeListener {
	
	private static final String TAG ="DataCollect:MainActivity";
	
	private DataCollectService mService;
	private CommunicationService commService;
	private Intent intent = null;
	private Intent commIntent=null;
	private boolean mBound = false;
	private boolean commBound = false;
	private Button communicationButton;
	private Button measureButton;
	
	private UploadTaskQueue queue = UploadTaskQueue.instance(this);
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        communicationButton = (Button)findViewById(R.id.buttonCommunicationStop);
        measureButton = (Button)findViewById(R.id.buttonStop);
                        
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
		
		intent = new Intent(this, DataCollectService.class);
		commIntent = new Intent(this,CommunicationService.class); 
		
		// Start service if not already started.
		if (!isServiceRunning(DataCollectService.class.getName())){
			this.startService(intent);
		}
		if (!isServiceRunning(CommunicationService.class.getName())){
			this.startService(commIntent);
		}
		
		checkWifiAvaiable();
		
		this.copyHtmlToSd();
    }
    
	private void checkWifiAvaiable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null ) {
			if(!wifi.isAvailable()) // Wifi is disabled, notify user and navigate to settings.
			{
				AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
				alertbox.setTitle("Wi-Fi state");
				alertbox.setMessage("Wi-Fi kapcsolat nincs engedélyezve. Kívánja engedélyezni? Enélkül a kommunikációs modul nem tudja feladatát elvégezni.");
				alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
				alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						 // No
						}
					});
				alertbox.show();				
			}			
		}
		
	}
	
	@Override
	protected void onStart() {		
		super.onStart();
		if (isServiceRunning(DataCollectService.class.getName())){ // PendingIntent causes binding without a running service.
			this.bindService(intent, mConnection, 0);
		}
		if (isServiceRunning(CommunicationService.class.getName())){
			this.bindService(commIntent, commConnection, 0);
		}
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
		intent = new Intent(this, DetailsActivity.class);
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
			//sending the listener availability in a bundle
			for (String sharedKey : DataCollectService.sharedPrefKeys){
				//if listener does not exist, then put true
				i.putExtra(sharedKey, this.mService.getListener(sharedKey)==null ? true : 
					this.mService.getListener(sharedKey).isAvailable());
			}
			startActivityForResult(i, 0);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		//finding which shared preference was changed, and register/unregister according to the value
		for (String sharedKey : DataCollectService.sharedPrefKeys){
			if (sharedKey.equals(key)) {
				IListener listener = mService.getListener(sharedKey);
				if (listener != null){
					//register/unregister listener
					if (sharedPreferences.getBoolean(sharedKey, false)){
						if (listener.isAvailable()){ listener.register(); }
					}					
					else{
						if (listener.isAvailable()){ listener.unregister(); }
					}	
				}
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
		switch (v.getId())
		{
		case R.id.buttonStop:
			if (isServiceRunning(DataCollectService.class.getName())){
				stopService(intent);
				if (mBound) {
		            unbindService(mConnection);
		            mBound = false;
		        }				
				measureButton.setText("Mérés indítása");
			}
			else{
				startService(intent);
				this.bindService(intent, mConnection, 0);
				
				measureButton.setText("Mérés leállítása");				
			}
			break;
		case R.id.buttonCommunicationStop:
			if (isServiceRunning(CommunicationService.class.getName())){
				stopService(commIntent);
				if (commBound) {
		            unbindService(commConnection);
		            commBound = false;
		        }
				
				communicationButton.setText("Kommunikáció indítása");
			}
			else{
				startService(commIntent);
				this.bindService(commIntent, commConnection, 0);
				
				communicationButton.setText("Kommunikáció leállítása");				
			}
			break;
		}
	}
	
	public void communicationDetailsClicked(View view){
		Intent i = new Intent(this, CommunicationActivity.class);
		this.startActivity(i);
	}
	
	//starting CameraActivity to take pictures
	public void uploadImages(View v){				
		//TODO get permission
		
		intent = new Intent(this, CameraActivity.class);
		this.startActivity(intent);
	}
	
	public void sendData(View v){
		//send here some data
		
		Log.d(TAG, "Sending some columns");
		this.queue.add(new DataUploadTask(this, "AccelerationData", 1, Arrays.asList("id", "timestamp", "accX", "accY")));
		
		Log.d(TAG, "Sending all columns");
		this.queue.add(new DataUploadTask(this, "LightData", 2));
		
		Log.d(TAG, "Sending all columns after date");		
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy/MM/dd").parse("2013/09/01");
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
		}
		this.queue.add(new DataUploadTask(this, "AccelerationData", 3, date));
		
		Log.d(TAG, "Sending buggy name");
		this.queue.add(new DataUploadTask(this, "NotExists", 4));
		
		Log.d(TAG, "Sending some columns after date");
		this.queue.add(new DataUploadTask(this, "AccelerationData", 5, date, Arrays.asList("id")));
	}
	
	public void loadJavascript(View v){
		
		//Testing AlgorithmActivity
		intent = new Intent(this, AlgorithmActivity.class);
		this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}
	
	/**
	 * Helper method to copy the sensors.html file from /assets to the sd card
	 */
	public void copyHtmlToSd() {

		AssetManager am = this.getAssets();
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			// Create new file to copy into.
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/DistributedAlgos/sensors.html");
			file.createNewFile();
			is = am.open("sensors.html");
			fos = new FileOutputStream(file);

			FileUtils.copy(is, fos);

		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
