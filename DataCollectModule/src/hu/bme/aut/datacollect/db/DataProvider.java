package hu.bme.aut.datacollect.db;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.datacollect.entity.IData;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DataProvider implements IDataProvider{
	
	private static final String TAG ="DataCollect:DataProvider";
	
	private DatabaseHelper dbHelper;
	
	public DataProvider(Context context){
		this.dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}
	
	@Override
	public <T extends IData> JSONObject getAllData(Class<T> clazz, String reqId, List<String> params) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryForAll();
		return IData.toJSONObject(list, reqId, params);
	}

	@Override
	public <T extends IData> JSONObject getDataAfterTimestamp(Class<T> clazz, String reqId, long timestamp, List<String> params) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryAfterTimestamp(timestamp);
		return IData.toJSONObject(list, reqId, params);
	}

	@Override
	public void close() throws IOException {
		if (dbHelper != null){
			OpenHelperManager.releaseHelper();
		}		
	}
	
	@Override
	public JSONObject getAllData(String name, String reqId) {		
		return this.getAllDataParams(name, reqId, null);
	}
	
	@Override
	public JSONObject getAllDataParams(String name, String reqId, List<String> params) {
		
		Class clazz;
		try {
			clazz = Class.forName("hu.bme.aut.datacollect.entity." + name);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		return this.getAllData(clazz, reqId, params);
	}
	
	@Override
	public JSONObject getDataAfterDate(String name, String reqId, Date date){				
		return this.getDataAfterDate(name, reqId, date, null);
	}

	@Override
	public JSONObject getDataAfterDate(String name, String reqId, Date date,
			List<String> params) {
		
		Class clazz;
		try {
			clazz = Class.forName("hu.bme.aut.datacollect.entity." + name);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
		
		return this.getDataAfterTimestamp(clazz, reqId, date.getTime(), params);
	}

	@Override
	public RequestLogData getRequestLogDataById(int id) {
		
		DaoBase<RequestLogData> dao = this.dbHelper.getDaoBase(RequestLogData.class);
		return dao.queryForId(id);
	}

	@Override
	public int createResponseLogData(ResponseLogData data) {
		
		DaoBase<ResponseLogData> dao = this.dbHelper.getDaoBase(ResponseLogData.class);
		return dao.create(data);
	}

	@Override
	public int updateRequestLogData(int id, String statuscode) {
		DaoBase<RequestLogData> dao = this.dbHelper.getDaoBase(RequestLogData.class);
		RequestLogData data = dao.queryForId(id);
		data.setStatusCode(statuscode);
		return dao.update(data);
	}
}
