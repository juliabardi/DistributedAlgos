package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.R;
import hu.bme.aut.datacollect.db.DataCollectDao;
import hu.bme.aut.datacollect.receiver.LocationProvider;
import hu.bme.aut.datacollect.receiver.SensorsListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
	private LocationProvider locProvider;
	private SensorsListener sensorsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        //instantiate class, it will register for loc updates
        locProvider = new LocationProvider(this);
        
        //instantiate to register
        sensorsListener = new SensorsListener(this);
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
		
		super.onDestroy();
	}


	@Override
	protected void onPause() {
		
		locProvider.unregisterListener();
		sensorsListener.unregisterListener();
		
		super.onPause();
	}
    
	
    
}
