package hu.bme.aut.datacollect.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class FileUtils {
	
	private static final String TAG = "DataCollect:FileUtils";
	
	public static String getStringFromStream(InputStream in){
		
		String s = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			s = sb.toString();
			Log.d(TAG, "Javascript file read succeeded.");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());	
		}
		return s;
	}

	public static String getStringFromFile(String filePath) {
		File fl = new File(filePath);
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fl);
			return getStringFromStream(fin);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());			
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		return null;
	}
}
