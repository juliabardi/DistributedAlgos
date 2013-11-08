package hu.bme.aut.datacollect.upload;

import hu.bme.aut.communication.GCM.RequestParams;

import org.json.JSONObject;

import android.content.Context;

public class DataUploadTask extends UploadTask {
	
	private static final long serialVersionUID = -4350943122852060571L;

	static {
		TAG = "DataCollect:DataUploadTask";
	}
	
	public DataUploadTask(Context context, RequestParams requestParams) {
		super(context, requestParams);
	}

	@Override
	public void execute(final Callback callback) {
		super.execute(callback);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				JSONObject result;
				
				if (rParams.getQueryDate() == null){
					result = dataProvider.getAllDataParams(rParams.getDataType(), rParams.getReqId(), rParams.getParams());
				}
				else {
					result = dataProvider.getDataAfterDate(rParams.getDataType(), rParams.getReqId(), rParams.getQueryDate(), rParams.getParams());
				}
				
				if (result == null){
					errorOccured("No data available.");
					return;
				}
				responseLog.setResponseSent(System.currentTimeMillis());
				httpManager.sendPostRequest(rParams.getAddress(), result.toString(), rParams.getPort());				
				
			}}).start();
	}

}
