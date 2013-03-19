package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.CallData;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class CallDao extends RuntimeExceptionDao<CallData, Integer>{

	public CallDao(Dao<CallData, Integer> dao) {
		super(dao);
	}

}
