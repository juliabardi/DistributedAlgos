package hu.bme.aut.datacollect.db;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class DaoBase<T, ID> extends BaseDaoImpl<T, ID> {

	protected DaoBase(ConnectionSource connectionSource, Class<T> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	@Override
	public int create(T data) {
		try {
			return super.create(data);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
