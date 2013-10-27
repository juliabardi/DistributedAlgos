package hu.bme.aut.datacollect.upload;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class ImageUploadTask extends UploadTask {

	private static final long serialVersionUID = -8650032781289859212L;

	static {
		TAG = "DataCollect:ImageUploadTask";
	}
	
	private File file;
	
	private long timestamp;
	private String port;

	public ImageUploadTask(File file, String address, String port, String reqId, long timestamp) {
		super(address, reqId);
		this.file = file;
		this.timestamp = timestamp;
		this.port=port;
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
					json.put("id", reqId);
					json.put("type", "binary");
					json.put("size", imageBytes.length);
					json.put("timestamp", timestamp);
					json.put("encoding", "base64");
					Log.d(TAG, json.toString()); //log out before adding the binary part
					json.put("binary", new String(Base64.encode(imageBytes, Base64.DEFAULT)));
										
					httpManager.sendPostRequest(address, json.toString(), port);
						
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
