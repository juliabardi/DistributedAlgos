package hu.bme.aut.communication;

import static hu.bme.aut.communication.GCM.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static hu.bme.aut.communication.GCM.CommonUtilities.EXTRA_MESSAGE;
import hu.bme.aut.datacollect.activity.DataCollectService;
import hu.bme.aut.datacollect.activity.MainActivity;
import hu.bme.aut.datacollect.activity.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
 * Wi-Fi specific:
 *  - Check if there is connection before sending msg to servers
 *  - If there is no connection at registering period
 * 		- If Wi-Fi disabled at startup
 * 			- Notify user
 * 		- Register to receive broadcast when there is valid connection
 * 			- When there is connection 			
 * 					- Unregister receiver
 * 					- Remove "Wi-Fi enable" notification if it is still there				
 *			 		- Try to register to servers
 *						- Notify user if registration fails to servers due to not valid Wi-Fi connection problem. (Server is down. )
 *	- If there is no connection during running
 *		- If Preferences changes do nothing, we will sync data with server if we were asked sg we dont provide (less traffic)
 *		- GCM request will arrive when we have connection again
 *		- If net connection is lost while we are trying to reply - either save request or server will ask later again if data is still necessary
 *	 
 *  TODO Handle errors coming back from started http msg-s to servers (e.g server is not available)
 * @author Eva Pataji
 *
 */
public class CommunicationService extends Service implements
		OnSharedPreferenceChangeListener {
	public static enum SyncronizationValues{
		TRUE,FALSE,NONE,SENDING
	}

	private final CommServiceBinder mBinder = new CommServiceBinder();
	private NotificationManager manager;
	private boolean isWifiListener = false;
	
	private Boolean registeredToDistributedAlgos=false; //dec_node
	private boolean isSyncronized=false; // If all need/offer data could be sent to server.
	private HashMap<String,SyncronizationValues> offerSyncronizationInfo;
	private MyResultReceiver theReceiver = null;
	
	public Boolean getRegisteredtoServer(){
		return GCMRegistrar.isRegisteredOnServer(this);
	}
	
	public Boolean getRegisteredtoGCM(){
		if(GCMRegistrar.getRegistrationId(this).equals("")) return false;
		else return true;
	}
	
	public Boolean getregisteredToDistributedAlgos(){
		return registeredToDistributedAlgos;
	}
	
	public HashMap getOfferSyncronizationInfo(){
		return offerSyncronizationInfo;
	} 

	public class CommServiceBinder extends Binder {

		public CommunicationService getService() {
			return CommunicationService.this;
		}
	}


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
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		theReceiver = new MyResultReceiver(new Handler());
	    theReceiver.setParentContext(this);
	    registeredToDistributedAlgos=false;
	    
		offerSyncronizationInfo = new HashMap<String,SyncronizationValues>();		
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		settings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		settings.registerOnSharedPreferenceChangeListener(this);
		setupForeground();

		registerReceiver(mGCMHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		if (IsWifiAvaiable()) {
			setupSyncronizationInfo(SyncronizationValues.NONE);
			registerToServers();
		} else // Start to listen to update when registration can be done.
		{
			setupSyncronizationInfo(SyncronizationValues.FALSE);			
			registerReceiver(mWifiConnectionChangedReceiver, new IntentFilter(
					WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
			isWifiListener=true;
		}
	}

	@Override
	public void onDestroy() {
		settings.unregisterOnSharedPreferenceChangeListener(this);
		manager.cancel(3);
		
		unregisterReceiver(mGCMHandleMessageReceiver);
		if(registeredToDistributedAlgos==true){
			unregisterFormDistributedAlgos();
		}
		
		if(!GCMRegistrar.getRegistrationId(this).equals("") && GCMRegistrar.isRegisteredOnServer(this)){
			unRegisterGCM();
		}
		
		if(isWifiListener){
			unregisterReceiver(mWifiConnectionChangedReceiver);
		}
		super.onDestroy();
	}
	
	private void unregisterFormDistributedAlgos(){
		registeredToDistributedAlgos=false;
		String url=Constants.getNodeServerAddress(this) + Constants.UNREGISTER;
		sendJobToNodeService(Constants.UNREGISTER,Constants.UNREGISTER,url, null);
	}
	
	private void setupSyncronizationInfo(SyncronizationValues syncValue){
		Map<String, ?> keys = settings.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String key = entry.getKey();
			if (DataCollectService.sharedPrefKeys.contains(key)){
			offerSyncronizationInfo.put(key,syncValue);
			}
		}
		
	}

	private void registerToServers() {
		Log.i(this.getClass().getName(), "Registering to servers.");
		registerPeer();
		registerGCM();
	}

	private boolean IsWifiAvaiable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null) {
			if (wifi.isAvailable() && wifi.isConnected()) {
				return true;
			} else if (!wifi.isAvailable()) // If wifi is disabled again send
											// notification to user
			{
				Intent notificationIntent = new Intent(
						Settings.ACTION_WIFI_SETTINGS);
				PendingIntent pendingIntent = PendingIntent.getActivity(this,
						0, notificationIntent, 0);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("CommunicationModule")
						.setContentText("Wifi nincs engedélyezve.")
						.setOngoing(true).setAutoCancel(true)
						.setContentIntent(pendingIntent);

				manager.notify(3, builder.build());

				return false;
			}
		}
		return false;
	}

	private void setupForeground() {

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("CommunicationModule")
				.setContentText("Kommunikációs modul engedélyezve.")
				.setOngoing(true).setContentIntent(pendingIntent);

		this.startForeground(2, builder.build());
	}

	/**
	 * Register peer need and offers when starting this service.
	 */
	private void registerPeer() {
		ArrayList<String> offerList = new ArrayList<String>();
		Map<String, ?> keys = settings.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String key = entry.getKey();
			if (DataCollectService.sharedPrefKeys.contains(key)){
				Boolean value = (Boolean) entry.getValue();
				if (value) {
					offerList.add(key);
				}
			}
		}

		JSONObject message = JsHelper.createMainBodyWithAlgos(Constants.ALGTYPE_DIST_ALGOS, JsHelper
				.createAlgoBody(JsHelper.createSimpleArray(offerList), null));

		sendJobToNodeService(Constants.REGISTER,Constants.REGISTER, Constants.getNodeServerAddress(this) + Constants.REGISTER, message.toString());
		setupSyncronizationInfo(SyncronizationValues.SENDING);
	}
	
	private void registerGCM(){
		sendJobToGcmService(Constants.REGISTER);
	}
	
	private void unRegisterGCM(){
		sendJobToGcmService(Constants.UNREGISTER);
	}

	/**
	 * Register or unregister an offer depending on user Settings.
	 * 
	 * @param key
	 *            offer
	 * @param value
	 *            true:register, false:unregister
	 */
	private void handleOffer(String key, boolean value) {
		if (IsWifiAvaiable()) {
			StringBuilder builder = new StringBuilder();
			builder.append(Constants.getNodeServerAddress(this));
			if (value == true) {
				builder.append(Constants.OFFER);
			} else {
				builder.append(Constants.UNREGISTER_OFFER);
			}
			builder.append("?" + Constants.ALGTYPE +"=" + Constants.ALGTYPE_DIST_ALGOS
					+ "&" + Constants.PARAM_NAME+"=" + key);

			String url = builder.toString();
			sendJobToNodeService(Constants.OFFER,key,url, null);
			offerSyncronizationInfo.put(key, SyncronizationValues.SENDING);
		} else {
			offerSyncronizationInfo.put(key, SyncronizationValues.FALSE);
			Log.i(this.getClass().getName(),
					String.format(
							"Could not notify server about offer state change: %s, Wi-Fi connection is not avaiable.",
							key));
		}
	}

	private void sendJobToNodeService(String messageType, String itemName,String url, String jsObj) {
		Intent intent = new Intent(this, NodeCommunicationIntentService.class);
		intent.putExtra(NodeCommunicationIntentService.URL, url);
		intent.putExtra(Constants.MESSAGE_TYPE, messageType);
		intent.putExtra(Constants.ITEM_NAME, itemName);
		intent.putExtra("resReceiver", theReceiver);

		if (jsObj != null) {
			intent.putExtra(NodeCommunicationIntentService.JSOBJ, jsObj);
		}
		this.startService(intent);
	}
	
	private void sendJobToGcmService(String msg){
		Intent intent = new Intent(this, GCMCommunicationIntentService.class);
		intent.putExtra(Constants.MESSAGE_TYPE, msg);
		this.startService(intent);
	}

	/*******Listeners********/
	
	/**
	 * Listen to changes in preferences.
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {
		if (DataCollectService.sharedPrefKeys.contains(key)){
			handleOffer(key, sharedPrefs.getBoolean(key, false));
		}
	}

	/**
	 * Listen to messages from GCM server.
	 */
	private final BroadcastReceiver mGCMHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			Log.i(this.getClass().getName(), newMessage);
		}
	};

	/**
	 * Listen to Wifi connection state.
	 */
	private BroadcastReceiver mWifiConnectionChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	
			if (intent.getAction().equals(
					WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
				if (intent.getBooleanExtra(
						WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {// There is net again, register if did not happen at startup.																			
					registerToServers();
					unregisterReceiver(mWifiConnectionChangedReceiver); // Dont listen when unnecessary.
					isWifiListener=false;
					manager.cancel(3);	// Cancel Wi-Fi notif if it is still there and was not clicked.
					Log.i(this.getClass().getName(), "Network connection establisted.");
				} else {
					// Wi-FI connection was lost.
				}
	
			}
		}
	};
	
	/**
	 * Log offer sync values from IntentService. 
	 */
	public class MyResultReceiver extends ResultReceiver {

	    private Context context = null;

	    protected void setParentContext (Context context) {
	        this.context = context;
	    }

	    public MyResultReceiver(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult (int resultCode, Bundle resultData) {
	    	Log.i(Constants.ALGTYPE_DIST_ALGOS,"DistributedAlgos server response processed.");
	    	registeredToDistributedAlgos = resultData.getBoolean(Constants.DISTRUBUTED_ALGOS_AVAIABLE_VALUE, false);
	        String messageType = resultData.getString(Constants.MESSAGE_TYPE);
	        if(messageType.equals(Constants.REGISTER)){
	        	if(resultData.getBoolean(Constants.ITEM_SYNC_VALUE, false)){
	        		registeredToDistributedAlgos=true;
	        		setupSyncronizationInfo(SyncronizationValues.TRUE);	        		
	        	}else{
	        		setupSyncronizationInfo(SyncronizationValues.FALSE);
	        	}
	        }
	        else if (messageType.equals(Constants.UNREGISTER)){
	        	// Do nothing, but could also set registeredToDistributedAlgos here false...
	        }
	        else{
	        	String itemName = resultData.getString(Constants.ITEM_NAME);
	        	if(resultData.getBoolean(Constants.ITEM_SYNC_VALUE, false)){
	        		offerSyncronizationInfo.put(itemName, SyncronizationValues.TRUE); // Managed to set requested value.
	        	}else{
	        		offerSyncronizationInfo.put(itemName, SyncronizationValues.FALSE); // Could not send to server.
	        	}
	        }
	    }
	}
}
