package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="screens", daoClass=DaoBase.class)
public class ScreenData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private String mode;
	
	public ScreenData(){}

	public ScreenData(long timestamp, String mode) {
		super();
		this.timestamp = timestamp;
		this.mode = mode;
	}

	public int getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return String.format(
				"CallData [id=%s, timestamp=%s, mode=%s]",
				id, timestamp, mode);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "mode");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("mode", String.valueOf(mode));
		return values;
	}
}
