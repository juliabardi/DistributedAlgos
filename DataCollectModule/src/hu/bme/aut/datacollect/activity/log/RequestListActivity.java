package hu.bme.aut.datacollect.activity.log;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.viewHelper.RequestLogsAdapter;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

/**
 * List of requests this device received as part of the Distributed Algorithms.
 * @author Eva Pataji
 *
 */
public class RequestListActivity extends OrmLiteBaseActivity<DatabaseHelper>
{
	private DaoBase<RequestLogData> requestLogDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.log_requestlist);
		requestLogDao = getHelper().getDaoBase(RequestLogData.class);
		RequestLogData requestLog = new RequestLogData(true, false, "ImageData", "fdf", Calendar.getInstance().getTimeInMillis());
		requestLog.setStatusCode("200");
		requestLogDao.create(requestLog);
		RequestLogData requestLog2 = new RequestLogData(true, true, "ScreenData", "fdf", Calendar.getInstance().getTimeInMillis());
		requestLogDao.create(requestLog2);
		RequestLogData requestLog3 = new RequestLogData(true, false, "BatteryData", "fdf", Calendar.getInstance().getTimeInMillis());
		requestLog3.setStatusCode("401.2");
		requestLogDao.create(requestLog3);
		
		List<RequestLogData> requestList = requestLogDao.queryForAll();
		if(requestList.size()==0){
			((TextView)findViewById(R.id.textViewNoData)).setVisibility(View.VISIBLE);
		}
		else{
			ListView mList = (ListView)findViewById(R.id.listViewRequests);
			RequestLogsAdapter adapter = new RequestLogsAdapter(this, requestList);
			mList.setAdapter(adapter);
		}
	}
	
}
