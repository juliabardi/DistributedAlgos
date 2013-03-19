package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.R;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.receiver.IncomingCallReceiver;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.OutgoingCallReceiver;
import hu.bme.aut.datacollect.receiver.SensorsListener;
import hu.bme.aut.datacollect.receiver.SensorsListener.Sensors;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	
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
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    
	public void onToggleClicked(View v){
		
		ToggleButton toggle = (ToggleButton)v;

		switch (v.getId()) {
		case R.id.toggleButtonAcceleration:
			if (toggle.isChecked()) {
				sensorsListener.registerListener(Sensors.ACCELEROMETER);
			} else {
				sensorsListener.unregisterListener(Sensors.ACCELEROMETER);
			}
			break;
		case R.id.toggleButtonLight:
			if (toggle.isChecked()) {
				sensorsListener.registerListener(Sensors.LIGHT);
			} else {
				sensorsListener.unregisterListener(Sensors.LIGHT);
			}
			break;
		case R.id.toggleButtonTemperature:
			if (toggle.isChecked()) {
				sensorsListener.registerListener(Sensors.TEMPERATURE);
			} else {
				sensorsListener.unregisterListener(Sensors.TEMPERATURE);
			}
			break;
		case R.id.toggleButtonFineLocation:
			if (toggle.isChecked()) {
				locProvider.registerListener();
			} else {
				locProvider.unregisterListener();
			}
			break;
		case R.id.toggleButtonCalls:
			if (toggle.isChecked()) {
				this.registerReceiverIncoming();
				this.registerReceiverOutgoing();
			} else {
				this.unregisterReceiverIncoming();
				this.unregisterReceiverOutgoing();
			}
			break;
		}
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
    
	public void onTvClicked(View v){
		//navigate to the details
		Intent intent = new Intent(this, DetailsActivity.class);
		this.startActivity(intent);
	}
}
