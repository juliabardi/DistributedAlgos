package hu.bme.aut.communication;

import static hu.bme.aut.communication.GCM.CommonUtilities.SENDER_ID;
import hu.bme.aut.communication.GCM.ServerUtilities;
import hu.bme.aut.datacollect.activity.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import static hu.bme.aut.communication.GCM.CommonUtilities.SENDER_ID;
import hu.bme.aut.communication.GCM.ServerUtilities;
import com.google.android.gcm.GCMRegistrar;


import com.google.android.gcm.GCMRegistrar;

/**
 *  Send registration/unregistration to the dec_admin GCM server.
 * @author Eva
 *
 */
public class GCMCommunicationIntentService  extends IntentService{

	private String TAG = GCMCommunicationIntentService.class.getName();
	
	public GCMCommunicationIntentService() {
		super("GcmCommunicationIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String messageType = intent.getStringExtra(Constants.MESSAGE_TYPE);
		
		if(messageType.equals(Constants.REGISTER)){
			Log.i(TAG,"Registering to GCM." );
			registerGCM();
		}else if (messageType.equals(Constants.UNREGISTER)){
			Log.i(TAG,"Unregistering from GCM." );
			unregisterGCM();
		}
		
	}
	
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}
	
	private void unregisterGCM(){
		if(!GCMRegistrar.getRegistrationId(this).equals("") && GCMRegistrar.isRegisteredOnServer(this)){
			Log.i(this.getClass().getName(), "Unregistering from dec_admin third party server.");
			ServerUtilities.unregister(this, GCMRegistrar.getRegistrationId(this)); // In case of server restart connect to the GCM server again, but dont unregister from GCM service.
		// Documentation says that an app unistall will take care of unregistering from GCM, also not recommended to register too often.
		// http://developer.android.com/google/gcm/adv.html
//		if (GCMRegistrar.isRegisteredOnServer(this)) {
//			GCMRegistrar.unregister(this);
//			GCMRegistrar.onDestroy(getApplicationContext()); // To handle
//																// exception
//																// error.
//		}
		}
	}
	
	/**
	 * Registering to the GCM push notification service.
	 */
	private void registerGCM() {
		checkNotNull(Constants.getGCMServerAddress(this), "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.i(this.getClass().getName(), "Already registered.");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				
						Boolean registered = ServerUtilities.register(this,
								regId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						
						if (!registered) {
							GCMRegistrar.unregister(this);
						}
					}
			}
	}
}
