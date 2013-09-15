package hu.bme.aut.datacollect.imageupload;

import hu.bme.aut.communication.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageUploadTask extends UploadTask {

	private static final long serialVersionUID = -8650032781289859212L;

	private File file;
	
	static {
		TAG = "DataCollect:ImageUploadTask";
	}

	public ImageUploadTask(File file) {
		this.file = file;
	}

	@Override
	public void execute(final Callback callback) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				
				Log.d(TAG, "Uploading file: " + file.getAbsolutePath());
				ImageUploadTask.this.mCallback = callback;
				
				//write to json and upload
				byte[] message = ImageUploadTask.this.writeMessage();
				if (message == null){
					Log.d(TAG, "Failed to create message.");
					return;
				}
				
				httpManager.sendPostRequest(Constants.NodeServerAddress, message);

				//delete the file in any case
				file.delete();
			}
		}).start();
	}

	// write the image to a message of json + \0 + binary image
	private byte[] writeMessage() {

		InputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			bos = new ByteArrayOutputStream();
			while (is.available() > 0) {
				bos.write(is.read());
			}
			byte[] imageBytes = bos.toByteArray();

			JSONObject json = new JSONObject();
			json.put("name", "ImageData");
			json.put("type", "binary");
			json.put("size", imageBytes.length);
			Log.d(TAG, json.toString());
			
			bos.reset();
			bos.write(json.toString().getBytes());
			bos.write("\0".getBytes());
			bos.write(imageBytes);
			//Log.d(TAG, bos.toString());
			return bos.toByteArray();
					
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
			}
			try {
				bos.close();
			} catch (IOException ie) {
			}
		}
		return null;
	}
}
