package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.TemperatureData;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class TemperatureDao extends RuntimeExceptionDao<TemperatureData, Integer>{

	public TemperatureDao(Dao<TemperatureData, Integer> dao) {
		super(dao);
	}
}
