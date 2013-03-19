package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.AccelerationData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class AccelerationDao extends DaoBase<AccelerationData, Integer>{

	public AccelerationDao(ConnectionSource connectionSource,
			Class<AccelerationData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
	
}
