package hu.bme.aut.datacollect.upload;

import hu.bme.aut.communication.GCM.RequestParams;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class ImageUploadTask extends UploadTask {

	private static final long serialVersionUID = -8650032781289859212L;

	static {
		TAG = "DataCollect:ImageUploadTask";
	}
	
	private File file;
	
	private long timestamp;

	public ImageUploadTask(Context context, RequestParams rParams, File file, long timestamp) {
		super(context, rParams);
		this.file = file;
		this.timestamp = timestamp;
	}

	@Override
	public void execute(final Callback callback) {
		super.execute(callback);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				Log.d(TAG, "Uploading file: " + file.getAbsolutePath());
				
				//write to json and upload
				JSONObject json = new JSONObject();
				
				InputStream is = null;
				ByteArrayOutputStream bos = null;
				try {
					is = new BufferedInputStream(new FileInputStream(file));
					bos = new ByteArrayOutputStream();
					while (is.available() > 0) {
						bos.write(is.read());
					}
					byte[] imageBytes = bos.toByteArray();

					json.put("name", "ImageData");
					json.put("id", rParams.getReqId());
					json.put("type", "binary");
					json.put("size", imageBytes.length);
					json.put("timestamp", timestamp);
					json.put("encoding", "base64");
					Log.d(TAG, json.toString()); //log out before adding the binary part
					json.put("binary", new String(Base64.encode(imageBytes, Base64.DEFAULT)));
									
					Log.d(TAG, "Sending reply to address: " + rParams.getAddress() + ", port: " + rParams.getPort());
					responseLog.setResponseSent(System.currentTimeMillis());
					httpManager.sendPostRequest(rParams.getAddress(), json.toString(), rParams.getPort());
						
					//catching outofmemoryerror, dont send anything then
				} catch (OutOfMemoryError e){
					Log.e(TAG, "OutofMemoryError: " + e.getMessage());
					callback.onFailure("OutofMemoryError");
				} catch (FileNotFoundException fe) {	
					Log.e(TAG, fe.getMessage());		
				} catch (IOException ie) {	
					Log.e(TAG, ie.getMessage());
				} catch (JSONException je) {	
					Log.e(TAG, je.getMessage());
				} finally {
					try {
						is.close();
					} catch (IOException ie) {
						Log.e(TAG, ie.getMessage());
					}
					try {
						bos.close();
					} catch (IOException ie) {
						Log.e(TAG, ie.getMessage());
					}
					//delete the file in any case
					file.delete();
				}
				
			}
		}).start();
	}

}
