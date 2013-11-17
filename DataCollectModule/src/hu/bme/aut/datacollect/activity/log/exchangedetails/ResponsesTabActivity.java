package hu.bme.aut.datacollect.activity.log.exchangedetails;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.viewHelper.ResponseLogsAdapter;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.j256.ormlite.dao.ForeignCollection;
/**
 * Details of the responses for one request.
 * @author Eva
 *
 */
public class ResponsesTabActivity extends
		OrmLiteBaseListActivity<DatabaseHelper> {
	private DaoBase<RequestLogData> requestLogDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_tab_responselist);

		long id = getIntent().getIntExtra(
				ExchangeDetailsTabActivity.REQUEST_ID, -1);
		if (id != -1) {
			requestLogDao = getHelper().getDaoBase(RequestLogData.class);
			RequestLogData request = requestLogDao.queryForId((int) id);
			if (request != null) {
				ForeignCollection<ResponseLogData> logs = request
						.getResponseLogs();
				if (logs != null && logs.size() > 0) {
					List<ResponseLogData> requestList = new ArrayList<ResponseLogData>(logs);
					ResponseLogsAdapter adapter = new ResponseLogsAdapter(this,
							requestList);
					setListAdapter(adapter);
				}
			}
		}
	}
}
