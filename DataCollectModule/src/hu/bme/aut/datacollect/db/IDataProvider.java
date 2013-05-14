package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.Closeable;
import java.util.List;

import org.json.JSONObject;

/**
 * Interface that provides the data existing in the database.
 * Extending Closeable, close() should be called after usage.
 * @author Juli
 *
 */
public interface IDataProvider extends Closeable  {

	/**
	 * Method providing all existing data of a given type.
	 * @param clazz the required data type
	 * @param reqId the id to identify the request
	 * @return list of the data found
	 */
	public <T extends IData> JSONObject getAllData(Class<T> clazz, int reqId);
	
	/**
	 * Method providing the data of a given type from the given timestamp
	 * @param clazz the required data type
	 * @param reqId the id to identify the request
	 * @param timestamp the required beginning timestamp
	 * @return list of the data found
	 */
	public <T extends IData> JSONObject getDataAfterTimestamp(Class<T> clazz, int reqId, long timestamp);
}
