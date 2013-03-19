package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.AccelerationData;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class AccelerationDao extends RuntimeExceptionDao<AccelerationData, Integer>{

	public AccelerationDao(Dao<AccelerationData, Integer> dao) {
		super(dao);
	}
}
