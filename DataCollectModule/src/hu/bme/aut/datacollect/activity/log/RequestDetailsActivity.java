package hu.bme.aut.datacollect.activity.log;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.db.DaoBase;
import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.util.List;

import android.os.Bundle;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;


/**
 * Details of one GCM request with the corresponding responses from this application.
 * @author Eva Pataji
 *
 */
public class RequestDetailsActivity extends OrmLiteBaseActivity<DatabaseHelper> {

	private DaoBase<RequestLogData> requestLogDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
}
