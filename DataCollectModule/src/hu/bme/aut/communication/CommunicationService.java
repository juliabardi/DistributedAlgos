package hu.bme.aut.communication;

import hu.bme.aut.datacollect.activity.MainActivity;
import hu.bme.aut.datacollect.activity.R;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
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
	private String serverAddress="http://10.0.2.2:3000/"; // Emulator localhost test
	
	
	
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
		super.onDestroy();
		settings.unregisterOnSharedPreferenceChangeListener(this);
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
		
		 sendJobToNodeService(serverAddress + REGISTER, message.toString());
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
			url=serverAddress +OFFER+"?"
	  			   	+"algType="+ALGTYPE+"&"
	  			   	+"name="+key;
		}
		else
		{
			url=serverAddress+UNREGISTER_OFFER+"?"
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

}
