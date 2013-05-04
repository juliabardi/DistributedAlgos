package hu.bme.aut.communication;

import static hu.bme.aut.communication.GCM.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static hu.bme.aut.communication.GCM.CommonUtilities.EXTRA_MESSAGE;
import static hu.bme.aut.communication.GCM.CommonUtilities.SENDER_ID;
import static hu.bme.aut.communication.GCM.CommonUtilities.SERVER_URL;
import hu.bme.aut.communication.GCM.ServerUtilities;
import hu.bme.aut.datacollect.activity.MainActivity;
import hu.bme.aut.datacollect.activity.R;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * Main service which
 * - sends messages to the node.js server via IntentService
 * - registers and unregisters app to push notification service provided by GCM
 * 
 * The service sends messages to the server if:
 * 	- there is a change in settings
 * When starting the service:
 * 	- we register the need-offer values from the settings
 *  - we register to the GCM to get push notifications
 *  TODO Handle errors
 *  TODO Unregister at the end
 *  TODO Other URL params
 *  TODO GCM
 *  TODO Check if there is net connection
 * @author Eva Pataji
 *
 */
public class CommunicationService extends Service implements OnSharedPreferenceChangeListener{

	private final CommServiceBinder mBinder = new CommServiceBinder();

	public class CommServiceBinder extends Binder {

		public CommunicationService getService() {
			return CommunicationService.this;
		}
	}
	
	// Server specific
	public static String NodeServerAddress="http://10.0.2.2:3000/"; // Emulator localhost test
	private static String TAG=CommunicationService.class.getSimpleName();
	
	
	// Message specific, only these messages can be sent to the server
	private static final String ALGTYPE="DistributedAlgos";
	private static final String REGISTER = "register";
	private static final String NEED = "need";
	private static final String OFFER = "offer";
	private static final String UNREGISTER_OFFER = "unregisterOffer";
	
	// SharedPref
	private SharedPreferences settings = null;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	/**
	 * Register offers and needs.
	 * TODO: Register to GCM.
	 */
	@Override
	public void onCreate() {
		super.onCreate(); 
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener(this);
		registerGCM();
		registerPeer();
		setupForeground();
	}
	
	
	
	private void setupForeground() {

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("DataCollectModule")
				.setContentText("Kommunikációs modul engedélyezve.").setOngoing(true)
				.setContentIntent(pendingIntent);

		this.startForeground(2, builder.build());
	}

	@Override
	public void onDestroy() {
		settings.unregisterOnSharedPreferenceChangeListener(this);

		if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.unregister(this);         
        GCMRegistrar.onDestroy(getApplicationContext()); // To handle exception error.
        
		super.onDestroy();
	}
	
	/**
	 * Register peer need and offers when starting this service.
	 */
	private void registerPeer()
	{
		ArrayList<String> offerList = new ArrayList<String>();
		Map<String,?> keys = settings.getAll();

		for(Map.Entry<String,?> entry : keys.entrySet()){
            String key= entry.getKey();
            Boolean value= (Boolean)entry.getValue();
            if(value)
            {
            	offerList.add(key);
            }
		 }
		
		 JSONObject message = JsHelper.createMainBodyWithAlgos(ALGTYPE, 
				 JsHelper.createAlgoBody(JsHelper.createSimpleArray(offerList),null));
		
		 sendJobToNodeService(NodeServerAddress + REGISTER, message.toString());
	}

	/**
	 * Register or unregister an offer depending on user Settings.
	 * @param key offer 
	 * @param value true:register, false:unregister
	 */
	private void handleOffer(String key, boolean value)
	{
		String url="";
		if(value == true)
		{
			url=NodeServerAddress +OFFER+"?"
	  			   	+"algType="+ALGTYPE+"&"
	  			   	+"name="+key;
		}
		else
		{
			url=NodeServerAddress+UNREGISTER_OFFER+"?"
	  			   	+"algType="+ALGTYPE+"&"
	  			   	+"name="+key;
		}
		
		sendJobToNodeService(url, null);
		
	}
	
	private void sendJobToNodeService(String url, String jsObj)
	{
		Intent intent = new Intent(this, NodeCommunicationIntentService.class);
		intent.putExtra(NodeCommunicationIntentService.URL, url);
		
		if(jsObj!=null){
			intent.putExtra(NodeCommunicationIntentService.JSOBJ, jsObj);	
		}
		this.startService(intent);
	}
	 
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key)
	{
		handleOffer(key,sharedPrefs.getBoolean(key,false));
	}
	
	
	// GCM - specific
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	/**
	 * Registering to the GCM push notification service.
	 */
	private void registerGCM()
	{
		checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        
        
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Log.i(TAG, "Already registered.");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }    

	}
	
	 private void checkNotNull(Object reference, String name) {
	        if (reference == null) {
	            throw new NullPointerException(
	                    getString(R.string.error_config, name));
	        }
	 }

	    private final BroadcastReceiver mHandleMessageReceiver =
	            new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
	           Log.i(TAG,newMessage);
	        }
	    };

	

}
