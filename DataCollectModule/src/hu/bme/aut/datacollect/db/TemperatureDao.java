package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.TemperatureData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class TemperatureDao extends DaoBase<TemperatureData>{

	public TemperatureDao(ConnectionSource connectionSource,
			Class<TemperatureData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

}
