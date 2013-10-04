package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.BatteryData;
import hu.bme.aut.datacollect.entity.CallData;
import hu.bme.aut.datacollect.entity.ConnectivityData;
import hu.bme.aut.datacollect.entity.GyroscopeData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.LocationData;
import hu.bme.aut.datacollect.entity.PackageData;
import hu.bme.aut.datacollect.entity.ProximityData;
import hu.bme.aut.datacollect.entity.OrientationData;
import hu.bme.aut.datacollect.entity.SmsData;
import hu.bme.aut.datacollect.entity.TemperatureData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{
	
	private static final String TAG ="DataCollect:DatabaseHelper";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DataCollectDb.db";
	
	@SuppressWarnings("rawtypes")
	Map<Class, DaoBase> map = new HashMap<Class, DaoBase>();

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		map.put(AccelerationData.class, null);
		map.put(CallData.class, null);
		map.put(LightData.class, null);
		map.put(LocationData.class, null);
		map.put(SmsData.class, null);
		map.put(TemperatureData.class, null);
		map.put(GyroscopeData.class, null);
		map.put(PackageData.class, null);
		map.put(BatteryData.class, null);
		map.put(ProximityData.class, null);
		map.put(OrientationData.class, null);
		map.put(ConnectivityData.class, null);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connSource) {
		
		try {
			Log.i(TAG, "onCreate");
			for (Class<Object> clazz : map.keySet()){
				TableUtils.createTable(connSource, clazz);
			}
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}
		
	}

	//TODO: is it right to drop the old db?
	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connSource, int oldVersion, int newVersion) {

		try {
			Log.i(TAG, "onUpgrade");
			
			for (Class<Object> clazz : map.keySet()){
				TableUtils.dropTable(connSource, clazz, true);
			}
			// after we drop the old databases, we create the new ones
			onCreate(database, connSource);
		} catch (SQLException e) {
			Log.e(TAG, "Can't drop databases", e);
			throw new RuntimeException(e);
		}

	}
	
	@Override
	public void close(){
		super.close();
		
		for (Class<Object> clazz : map.keySet()){
			map.put(clazz, null);
		}
	}

	@Override
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) {
		try {
			return super.getDao(clazz);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> DaoBase<T> getDaoBase(Class<T> clazz) {
		
		if (map.get(clazz) == null){
			map.put(clazz, (DaoBase<T>)this.getDao(clazz));
		}
		return map.get(clazz);
	}
	
}
