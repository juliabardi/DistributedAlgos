package hu.bme.aut.communication.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** 
 * Log responses to the GCM requests. 
 * @author Eva
 *
 */

@DatabaseTable(tableName="responselogs", daoClass=DaoBase.class)
public class ResponseLogData {

	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField(canBeNull = false, foreign = true)
	private RequestLogData requestLogId;
	
	// When the reply is ready to be sent.
	@DatabaseField(canBeNull=false)
	private long responseSent;
	
	// When our reply is sent to the requester and we get the statuscode.
	@DatabaseField(canBeNull=false)
	private long answerReceived;
	
	@DatabaseField(canBeNull=false)
	private  String statusCode;
	
	// Optional message from the requester.
	@DatabaseField(canBeNull=false)
	private  String answerParams;
	
	public ResponseLogData() {
		super();
	}
	
	public ResponseLogData(RequestLogData requestLogId,
			long responseSent, long answerReceived, String statusCode,
			String answerParams) {
		super();
		this.requestLogId = requestLogId;
		this.responseSent = responseSent;
		this.answerReceived = answerReceived;
		this.statusCode = statusCode;
		this.answerParams = answerParams;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RequestLogData getRequestLogId() {
		return requestLogId;
	}

	public void setRequestLogId(RequestLogData requestLogId) {
		this.requestLogId = requestLogId;
	}

	public long getResponseSent() {
		return responseSent;
	}

	public void setResponseSent(long responseSent) {
		this.responseSent = responseSent;
	}

	public long getAnswerReceived() {
		return answerReceived;
	}

	public void setAnswerReceived(long answerReceived) {
		this.answerReceived = answerReceived;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getAnswerParams() {
		return answerParams;
	}

	public void setAnswerParams(String answerParams) {
		this.answerParams = answerParams;
	}
}
