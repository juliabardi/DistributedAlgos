package hu.bme.aut.datacollect.activity.log.exchangedetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.utils.ViewDataUtils;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import android.os.Bundle;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

/**
 * Details of the request.
 * @author Eva
 *
 */
public class RequestDetailsTabActivity extends OrmLiteBaseActivity<DatabaseHelper> {
	private DaoBase<RequestLogData> requestLogDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_tab_requestdetails);
		long id=getIntent().getIntExtra(ExchangeDetailsTabActivity.REQUEST_ID,-1);
		if(id!=-1){
			requestLogDao = getHelper().getDaoBase(RequestLogData.class);
			RequestLogData request = requestLogDao.queryForId((int)id);
			if(request!=null){
				((TextView)findViewById(R.id.textViewOfferName)).setText(request.getOfferName());
				
				((TextView)findViewById(R.id.textViewRequestReceived)).setText(ViewDataUtils.getDateInString(request.getRequestReceived()));
				
				((TextView)findViewById(R.id.textViewValidRequest)).setText(request.getValidRequest()==true?"Igen":"Nem");
				
				((TextView)findViewById(R.id.textViewRequestId)).setText(ViewDataUtils.getStringValue(request.getRequestId()));
				
				((TextView)findViewById(R.id.textViewPeriodic)).setText(request.getPeriodic()==true?"Igen":"Nem");
				
				((TextView)findViewById(R.id.textViewRequestParams)).setText(ViewDataUtils.getStringValue(request.getRequestParams()));
			}
		}
		
	}
}
