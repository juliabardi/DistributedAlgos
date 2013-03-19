package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.LocationData;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LocationDao extends RuntimeExceptionDao<LocationData, Integer>{

	public LocationDao(Dao<LocationData, Integer> dao) {
		super(dao);
	}

}
