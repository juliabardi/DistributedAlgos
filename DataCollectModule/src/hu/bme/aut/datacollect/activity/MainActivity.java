package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.R;
import hu.bme.aut.datacollect.receiver.IncomingCallReceiver;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.OutgoingCallReceiver;
import hu.bme.aut.datacollect.receiver.SensorsListener;
import hu.bme.aut.datacollect.receiver.SensorsListener.Sensors;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private LocationProvider locProvider;
	private SensorsListener sensorsListener;
	private IncomingCallReceiver incomingReceiver;
	private OutgoingCallReceiver outgoingReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        //instantiate class, it will register for loc updates
        locProvider = new LocationProvider(this);
        
        //instantiate to register
        sensorsListener = new SensorsListener(this);
        
        incomingReceiver = new IncomingCallReceiver();
        outgoingReceiver = new OutgoingCallReceiver();
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
		
//		locProvider.unregisterListener();
//		sensorsListener.unregisterListener();
		
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
		
		IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
		this.registerReceiver(incomingReceiver, intentFilter);
	}
	
	public void unregisterReceiverIncoming(){
		
		this.unregisterReceiver(incomingReceiver);
	}
	
	public void registerReceiverOutgoing(){
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		this.registerReceiver(outgoingReceiver, intentFilter);
	}
	
	public void unregisterReceiverOutgoing(){
		
		this.unregisterReceiver(outgoingReceiver);
	}
    
	public void onTvClicked(View v){
		//navigate to the details
		Intent intent = new Intent(this, DetailsActivity.class);
		this.startActivity(intent);
	}
}
