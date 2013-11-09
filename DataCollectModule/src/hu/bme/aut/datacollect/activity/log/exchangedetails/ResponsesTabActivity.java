package hu.bme.aut.datacollect.activity.log.exchangedetails;

import java.util.List;

import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.viewHelper.ResponseLogsAdapter;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
/**
 * Details of the responses for one request.
 * @author Eva
 *
 */
public class ResponsesTabActivity extends OrmLiteBaseListActivity<DatabaseHelper>{
	private DaoBase<ResponseLogData> requestLogDao;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_tab_responselist);
		
		requestLogDao = getHelper().getDaoBase(ResponseLogData.class);
		
		List<ResponseLogData> requestList = requestLogDao.queryForAll();
		ResponseLogsAdapter adapter = new ResponseLogsAdapter(this, requestList);
		setListAdapter(adapter);
	}
}
