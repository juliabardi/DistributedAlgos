package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.SmsData;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public class SmsDao extends DaoBase<SmsData> {

	public SmsDao(ConnectionSource connectionSource,
			Class<SmsData> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
}
