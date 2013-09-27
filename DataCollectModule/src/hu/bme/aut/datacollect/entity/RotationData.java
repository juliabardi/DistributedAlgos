package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="rotations", daoClass=DaoBase.class)
public class RotationData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float rotA;
	@DatabaseField(canBeNull=false)
	private float rotB;
	@DatabaseField(canBeNull=false)
	private float rotC;
	
	public RotationData(){}
	
	public RotationData(long timestamp, float rotA, float rotB, float rotC) {
		super();
		this.timestamp = timestamp;
		this.rotA = rotA;
		this.rotB = rotB;
		this.rotC = rotC;
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
	
	public float getRotA() {
		return rotA;
	}

	public void setRotA(float rotA) {
		this.rotA = rotA;
	}

	public float getRotB() {
		return rotB;
	}

	public void setRotB(float rotB) {
		this.rotB = rotB;
	}

	public float getRotC() {
		return rotC;
	}

	public void setRotC(float rotC) {
		this.rotC = rotC;
	}

	@Override
	public String toString() {
		return String
				.format("RotationData [id=%s, timestamp=%s, rotA=%s, rotB=%s, rotC=%s]",
						id, timestamp, rotA, rotB, rotC);
	}

	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "rotA", "rotB", "rotC");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("rotA", String.valueOf(rotA));
		values.put("rotB", String.valueOf(rotB));
		values.put("rotC", String.valueOf(rotC));
		return values;
	}
	
}
