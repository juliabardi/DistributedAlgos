package hu.bme.aut.datacollect.activity.log;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.exchangedetails.ExchangeDetailsTabActivity;
import hu.bme.aut.datacollect.activity.log.viewHelper.RequestLogsAdapter;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

/**
 * List of requests this device received as part of the Distributed Algorithms.
 * @author Eva Pataji
 *
 */
public class RequestListActivity extends OrmLiteBaseListActivity<DatabaseHelper>
{
	private DaoBase<RequestLogData> requestLogDao;
	private DaoBase<ResponseLogData> responseLogDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.log_requestlist);
		requestLogDao = getHelper().getDaoBase(RequestLogData.class);
		responseLogDao = getHelper().getDaoBase(ResponseLogData.class);
        setList();
	}
	
	public void deleteLog(View v){
		try {
			responseLogDao.deleteBuilder().delete();
			requestLogDao.deleteBuilder().delete();
			setList();
		} catch (SQLException e) {
			Toast.makeText(this, "Az adatok törlése nem sikerült.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private void setList(){ // ORMLite notifyPropChange does not work on refresh...
		List<RequestLogData> requestList;
		try {
			requestList = requestLogDao.query(
					requestLogDao.queryBuilder().
					orderBy("requestReceived", false).prepare());
			if(requestList.size()>0){
				((LinearLayout)findViewById(R.id.header)).setVisibility(View.VISIBLE);
			}else{
				((LinearLayout)findViewById(R.id.header)).setVisibility(View.GONE);		
			}
				RequestLogsAdapter adapter = new RequestLogsAdapter(this, requestList);
				setListAdapter(adapter);

		} catch (SQLException e) {
			Toast.makeText(this, "Az adatok betöltése nem sikerült.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent();
		i.putExtra(ExchangeDetailsTabActivity.REQUEST_ID, ((RequestLogData)l.getAdapter().getItem(position)).getId());
		i.setClass(this, ExchangeDetailsTabActivity.class);
		this.startActivity(i);
	}
	
}
