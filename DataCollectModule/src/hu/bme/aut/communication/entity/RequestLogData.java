package hu.bme.aut.communication.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Log requests from the GCM server.
 * Reqest can be one-time or periodic. 
 * @author Eva
 *
 */
@DatabaseTable(tableName="requestlogs", daoClass=DaoBase.class)
public class RequestLogData {

	@DatabaseField(generatedId=true)
	private int id;
	
	// If request could be processed. If not examine offerRequest.
	@DatabaseField(canBeNull=false)
	private Boolean validRequest;
	
	// Message request id from the requester for follow-up purposes at the requester side.
	@DatabaseField(canBeNull=true)
	private String requestId;
	
	// This is a periodic or a one-time request.
	@DatabaseField(canBeNull=false)
	private Boolean periodic;
		
	// The name of the offer the requester asks for.
	@DatabaseField(canBeNull=false)
	private String offerName;
	
	// All of the requestParams for test purposes.
	@DatabaseField(canBeNull=false)
	private String requestParams;
	
	// The time this GCM intent was first received.
	@DatabaseField(canBeNull=false)
	private long requestReceived;

	// Don't join the tables all the time for this column if it is not a periodic request.
	// If the requester could process our message.
	@DatabaseField(canBeNull=true)
	private  String statusCode;
		
	// If periodic may be more than one result or if there was a retry in sending.
	@ForeignCollectionField(eager = false)
    ForeignCollection<ResponseLogData> responseLogs;

	public RequestLogData() {
		super();
	}
	
	public RequestLogData(Boolean validRequest, Boolean periodic,
			String offerName, String requestParams, long requestReceived) {
		super();
		this.validRequest = validRequest;
		this.periodic = periodic;
		this.offerName = offerName;
		this.requestParams = requestParams;
		this.requestReceived = requestReceived;
	}
	
	public  void setLogData(String requestId, Boolean periodic,
			String offerName) {
		this.requestId = requestId;
		this.periodic = periodic;
		this.offerName = offerName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Boolean getValidRequest() {
		return validRequest;
	}

	public void setValidRequest(Boolean validRequest) {
		this.validRequest = validRequest;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Boolean getPeriodic() {
		return periodic;
	}

	public void setPeriodic(Boolean periodic) {
		this.periodic = periodic;
	}

	public String getOfferName() {
		return offerName;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public long getRequestReceived() {
		return requestReceived;
	}

	public void setRequestReceived(long requestReceived) {
		this.requestReceived = requestReceived;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public ForeignCollection<ResponseLogData> getResponseLogs() {
		return responseLogs;
	}

	public void setResponseLogs(ForeignCollection<ResponseLogData> responseLogs) {
		this.responseLogs = responseLogs;
	}

}
