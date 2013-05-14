package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DataProvider implements IDataProvider{
	
	private DatabaseHelper dbHelper;
	private Context mContext;
	
	public DataProvider(Context context){
		this.mContext = context;
		this.dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	@Override
	public <T extends IData> JSONObject getAllData(Class<T> clazz, int reqId) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryForAll();
		return IData.toJSONObject(list, reqId);
	}

	@Override
	public <T extends IData> JSONObject getDataAfterTimestamp(Class<T> clazz, int reqId, long timestamp) {
		List<T> list = this.dbHelper.getDaoBase(clazz).queryAfterTimestamp(timestamp);
		return IData.toJSONObject(list, reqId);
	}

	@Override
	public void close() throws IOException {
		if (dbHelper != null){
			OpenHelperManager.releaseHelper();
		}		
	}

}
