package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.CallData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.LocationData;
import hu.bme.aut.datacollect.entity.TemperatureData;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DataCollectDb.db";
	
	private AccelerationDao accelerationDao = null;
	private CallDao callDao = null;
	private LightDao lightDao = null;
	private LocationDao locationDao = null;
	private TemperatureDao temperatureDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, AccelerationData.class);
			TableUtils.createTable(connectionSource, CallData.class);
			TableUtils.createTable(connectionSource, LightData.class);
			TableUtils.createTable(connectionSource, LocationData.class);
			TableUtils.createTable(connectionSource, TemperatureData.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
	}

	//TODO: is it right to drop the old db?
	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {

		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, AccelerationData.class, true);
			TableUtils.dropTable(connectionSource, CallData.class, true);
			TableUtils.dropTable(connectionSource, LightData.class, true);
			TableUtils.dropTable(connectionSource, LocationData.class, true);
			TableUtils.dropTable(connectionSource, TemperatureData.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}

	}
	
	public AccelerationDao getAccelerationDao(){
		if (accelerationDao == null) {
			accelerationDao = getDao(AccelerationData.class);
		}
		return accelerationDao;
	}
	
	public CallDao getCallDao(){
		if (callDao == null) {
			callDao = getDao(CallData.class);
		}
		return callDao;
	}
	
	public LightDao getLightDao(){
		if (lightDao == null) {
			lightDao = getDao(LightData.class);
		}
		return lightDao;
	}
	
	public LocationDao getLocationDao(){
		if (locationDao == null) {
			locationDao = getDao(LocationData.class);
		}
		return locationDao;
	}
	
	public TemperatureDao getTemperatureDao(){
		if (temperatureDao == null) {
			temperatureDao = getDao(TemperatureData.class);
		}
		return temperatureDao;
	}
	
	@Override
	public void close(){
		super.close();
		accelerationDao = null;
		callDao = null;
		lightDao = null;
		locationDao = null;
		temperatureDao = null;
	}

	@Override
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz)
			{
		try {
			return super.getDao(clazz);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
