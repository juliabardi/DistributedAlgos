package hu.bme.aut.datacollect.db;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class DaoBase<T> extends BaseDaoImpl<T, Integer> {
	
	private static final String TAG ="DataCollect:DaoBase";

	public DaoBase(ConnectionSource connectionSource, Class<T> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	@Override
	public int create(T data) {
		try {
			int rowId = super.create(data);
			Log.d(TAG, "Inserted row into " + dataClass.getSimpleName() + ", modified rows: " + rowId);
			return rowId;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<T> queryForAll() {
		try {
			return super.queryForAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T queryForId(Integer id){
		try {
			return super.queryForId(id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<T> queryAfterTimestamp(long timestamp){
		
		try {
			return this.queryBuilder().where().ge("timestamp", timestamp).query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public T queryLast(){
		
		try {
			return this.queryBuilder().orderBy("timestamp", false).limit(1L).queryForFirst();			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
