package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="RecurringRequest", daoClass=DaoBase.class)
public class RecurringRequest {
	
	@DatabaseField(canBeNull=true)
	private String reqId;
	@DatabaseField(canBeNull=false, id=true)
	private String ip;
	@DatabaseField(canBeNull=true)
	private String port;
	@DatabaseField(canBeNull=false)
	private int recurrence;
	@DatabaseField(canBeNull=true)
	private long lastSent;
	@DatabaseField(canBeNull=false)
	private String dataType;
	@DatabaseField(canBeNull=true)
	private String params;
	
	public RecurringRequest(){}
	
	public RecurringRequest(String reqId, String ip, String port, int recurrence,
			long lastSent, String dataType, String params) {
		super();
		this.ip = ip;
		this.port = port;
		this.recurrence = recurrence;
		this.lastSent = lastSent;
		this.dataType = dataType;
		this.params = params;
		this.reqId = reqId;
	}

	public RecurringRequest(String reqId, String ip, String port, int recurrence,
			long lastSent, String dataType) {
		super();
		this.ip = ip;
		this.port = port;
		this.recurrence = recurrence;
		this.lastSent = lastSent;
		this.dataType = dataType;
		this.reqId = reqId;
	}
	
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(int recurrence) {
		this.recurrence = recurrence;
	}

	public long getLastSent() {
		return lastSent;
	}

	public void setLastSent(long lastSent) {
		this.lastSent = lastSent;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + (int) (lastSent ^ (lastSent >>> 32));
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + recurrence;
		result = prime * result + ((reqId == null) ? 0 : reqId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecurringRequest other = (RecurringRequest) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (lastSent != other.lastSent)
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (recurrence != other.recurrence)
			return false;
		if (reqId == null) {
			if (other.reqId != null)
				return false;
		} else if (!reqId.equals(other.reqId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("RecurringRequest [reqId=%s, ip=%s, port=%s, recurrence=%s, lastSent=%s, dataType=%s, params=%s]",
						reqId, ip, port, recurrence, lastSent, dataType, params);
	}
	
}
