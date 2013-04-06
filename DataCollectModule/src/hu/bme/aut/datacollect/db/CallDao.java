package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.CallData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class CallDao extends DaoBase<CallData>{

	public CallDao(ConnectionSource connectionSource,
			Class<CallData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

}
