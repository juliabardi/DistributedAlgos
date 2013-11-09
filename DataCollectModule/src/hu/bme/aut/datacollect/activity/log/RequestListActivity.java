package hu.bme.aut.datacollect.activity.log;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.exchangedetails.ExchangeDetailsTabActivity;
import hu.bme.aut.datacollect.activity.log.viewHelper.RequestLogsAdapter;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

/**
 * List of requests this device received as part of the Distributed Algorithms.
 * @author Eva Pataji
 *
 */
public class RequestListActivity extends OrmLiteBaseListActivity<DatabaseHelper>
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
		RequestLogsAdapter adapter = new RequestLogsAdapter(this, requestList);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent();
		i.putExtra(ExchangeDetailsTabActivity.REQUEST_ID, id);
		this.startActivity(new Intent(this, ExchangeDetailsTabActivity.class));
	}
	
}
