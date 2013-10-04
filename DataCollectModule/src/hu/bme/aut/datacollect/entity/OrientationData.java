package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="orientations", daoClass=DaoBase.class)
public class OrientationData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float alpha;
	@DatabaseField(canBeNull=false)
	private float beta;
	@DatabaseField(canBeNull=false)
	private float gamma;
	
	public OrientationData(){}
	
	public OrientationData(long timestamp, float alpha, float beta, float gamma) {
		super();
		this.timestamp = timestamp;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
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

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getGamma() {
		return gamma;
	}

	public void setGamma(float gamma) {
		this.gamma = gamma;
	}

	@Override
	public String toString() {
		return String
				.format("RotationData [id=%s, timestamp=%s, alpha=%s, beta=%s, gamma=%s]",
						id, timestamp, alpha, beta, gamma);
	}

	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "alpha", "beta", "gamma");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("alpha", String.valueOf(alpha));
		values.put("beta", String.valueOf(beta));
		values.put("gamma", String.valueOf(gamma));
		return values;
	}
	
}
