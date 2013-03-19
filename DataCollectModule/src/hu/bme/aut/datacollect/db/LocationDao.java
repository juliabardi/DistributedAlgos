package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.LocationData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class LocationDao extends DaoBase<LocationData, Integer>{

	public LocationDao(ConnectionSource connectionSource,
			Class<LocationData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
