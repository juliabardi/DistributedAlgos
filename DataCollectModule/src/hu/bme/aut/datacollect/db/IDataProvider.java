package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.Closeable;
import java.util.List;

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
	 * @return list of the data found
	 */
	public <T extends IData> List<T> getAllData(Class<T> clazz);
	
	/**
	 * Method providing the data of a given type from the given timestamp
	 * @param clazz the required data type
	 * @param timestamp the required beginning timestamp
	 * @return list of the data found
	 */
	public <T extends IData> List<T> getDataAfterTimestamp(Class<T> clazz, long timestamp);
}
