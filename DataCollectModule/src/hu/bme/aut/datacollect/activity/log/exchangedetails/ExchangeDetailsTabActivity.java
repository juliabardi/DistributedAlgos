package hu.bme.aut.datacollect.activity.log.exchangedetails;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.io.Closeable;
import java.io.IOException;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;


/**
 * Details of one GCM request with the corresponding responses from this application.
 * @author Eva Pataji
 *
 */
@SuppressWarnings("deprecation")
public class ExchangeDetailsTabActivity extends TabActivity{

	public static final String REQUEST_ID="reqId";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_tab_exchange);
		TabHost tabHost = getTabHost(); 
		
		Intent intentRequest = new Intent()
			.putExtra(REQUEST_ID, getIntent().getIntExtra(REQUEST_ID,-1))
			.setClass(this, RequestDetailsTabActivity.class);
		TabSpec tabSpecRequest = tabHost
		  .newTabSpec("RequestTab")
		  .setIndicator(setupTab("Kérés"))
		  .setContent(intentRequest);
		
		
		
		Intent intentRequest2 = new Intent()
				.putExtra(REQUEST_ID, getIntent().getIntExtra(REQUEST_ID,-1))
				.setClass(this, ResponsesTabActivity.class);
		TabSpec tabSpecResponses = tabHost
		  .newTabSpec("ResponseTab")
		  .setIndicator(setupTab("Teljesítés"))
		  .setContent(intentRequest2);
		
		tabHost.addTab(tabSpecRequest);
		tabHost.addTab(tabSpecResponses);
		
		tabHost.setCurrentTab(0);
		
	}
	
	private LinearLayout setupTab(String title)
	{
		LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.log_tab, null);
		TextView t = (TextView)layout.findViewById(R.id.textViewTitle);
		t.setText(title);
		return layout;
	}
		
}
