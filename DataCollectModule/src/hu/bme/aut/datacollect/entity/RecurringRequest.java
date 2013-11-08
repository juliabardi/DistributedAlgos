package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="RecurringRequest", daoClass=DaoBase.class)
public class RecurringRequest {
	
	@DatabaseField(canBeNull=false, id=true)
	private String reqId;
	@DatabaseField(canBeNull=false, uniqueCombo=true)
	private String ip;
	@DatabaseField(canBeNull=true, uniqueCombo=true)
	private String port;
	@DatabaseField(canBeNull=false)
	private String protocol;
	@DatabaseField(canBeNull=false)
	private int recurrence;
	@DatabaseField(canBeNull=true)
	private long lastSent;
	@DatabaseField(canBeNull=false, uniqueCombo=true)
	private String dataType;
	@DatabaseField(canBeNull=true)
	private String params;
	@DatabaseField(canBeNull=false)
	private int idRequestLog;
	
	public RecurringRequest(){}
	
	public RecurringRequest(String reqId, String ip, String port, String protocol, int recurrence,
			long lastSent, String dataType, String params, int idRequestLog) {
		super();
		this.ip = ip;
		this.port = port;
		this.recurrence = recurrence;
		this.lastSent = lastSent;
		this.dataType = dataType;
		this.params = params;
		this.reqId = reqId;
		this.protocol = protocol;
		this.idRequestLog = idRequestLog;
	}

	public RecurringRequest(String reqId, String ip, String port, String protocol, int recurrence,
			long lastSent, String dataType, int idRequestLog) {
		super();
		this.ip = ip;
		this.port = port;
		this.recurrence = recurrence;
		this.lastSent = lastSent;
		this.dataType = dataType;
		this.reqId = reqId;
		this.protocol = protocol;
		this.idRequestLog = idRequestLog;
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getIdRequestLog() {
		return idRequestLog;
	}

	public void setIdRequestLog(int idRequestLog) {
		this.idRequestLog = idRequestLog;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + idRequestLog;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + (int) (lastSent ^ (lastSent >>> 32));
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
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
		if (idRequestLog != other.idRequestLog)
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
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
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
				.format("RecurringRequest [reqId=%s, ip=%s, port=%s, protocol=%s, recurrence=%s, lastSent=%s, dataType=%s, params=%s, idRequestLog=%s]",
						reqId, ip, port, protocol, recurrence, lastSent,
						dataType, params, idRequestLog);
	}
	
}
