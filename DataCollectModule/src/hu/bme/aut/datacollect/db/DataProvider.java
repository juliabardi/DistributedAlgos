package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.IOException;
import java.util.List;

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
	public <T extends IData> List<T> getAllData(Class<T> clazz) {
		return this.dbHelper.getDaoBase(clazz).queryForAll();
	}

	@Override
	public <T extends IData> List<T> getDataAfterTimestamp(Class<T> clazz, long timestamp) {
		return this.dbHelper.getDaoBase(clazz).queryAfterTimestamp(timestamp);
	}

	@Override
	public void close() throws IOException {
		if (dbHelper != null){
			OpenHelperManager.releaseHelper();
		}		
	}

}
