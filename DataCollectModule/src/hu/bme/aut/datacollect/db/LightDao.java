package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.LightData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class LightDao extends DaoBase<LightData, Integer> {

	public LightDao(ConnectionSource connectionSource,
			Class<LightData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

}
