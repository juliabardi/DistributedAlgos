package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.LightData;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class LightDao extends RuntimeExceptionDao<LightData, Integer> {

	public LightDao(Dao<LightData, Integer> dao) {
		super(dao);
	}

}
