package hu.bme.aut.datacollect.activity.log.exchangedetails;

import hu.bme.aut.datacollect.activity.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * Details of the request.
 * @author Eva
 *
 */
public class RequestDetailsTabActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_tab_requestdetails);
	}

}
