package hu.bme.aut.datacollect.db;

import java.sql.SQLException;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class DaoBase<T, ID> extends BaseDaoImpl<T, ID> {

	public DaoBase(ConnectionSource connectionSource, Class<T> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	@Override
	public int create(T data) {
		try {
			int rowId = super.create(data);
			Log.d("hu.bme.aut.sqlite", "Inserted row into " + dataClass.getSimpleName() + ", modified rows: " + rowId);
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
	
	

}
