package hu.bme.aut.communication;

import hu.bme.aut.datacollect.activity.MainActivity;
import hu.bme.aut.datacollect.activity.R;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class CommunicationService extends Service{

	private final CommServiceBinder mBinder = new CommServiceBinder();

	public class CommServiceBinder extends Binder {

		public CommunicationService getService() {
			return CommunicationService.this;
		}
	}
	
	private String serverAddress="http://10.0.2.2:3000"; //emulator localhost test
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	 * Register peer need and offers when statring this service.
	 */
	private void registerPeer()
	{
		 new Thread(new Runnable() {
			    public void run() {
			      HttpManager.sendPostRequest(serverAddress,JsHelper.registerOfferNeed() );
			    }
			  }).start();

		
	}

}
