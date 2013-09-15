package hu.bme.aut.datacollect.db;

import hu.bme.aut.datacollect.entity.IData;

import java.io.Closeable;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

/**
 * Interface that provides the data existing in the database.
 * Extending Closeable, close() should be called after usage.
 * @author Juli
 *
 */
public interface IDataProvider extends Closeable  {
	
	public <T extends IData> JSONObject getAllData(Class<T> clazz, int reqId, List<String> params);
	
	public <T extends IData> JSONObject getDataAfterTimestamp(Class<T> clazz, int reqId, long timestamp, List<String> params);
	
	public JSONObject getAllData(String name, int reqId);
	
	public JSONObject getAllData(String name, int reqId, List<String> params);
	
	public JSONObject getDataAfterDate(String name, int reqId, Date date);
	
	public JSONObject getDataAfterDate(String name, int reqId, Date date, List<String> params);
}
