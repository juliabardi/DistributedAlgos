package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.upload.CameraPreview;
import hu.bme.aut.datacollect.upload.ImageUploadTask;
import hu.bme.aut.datacollect.upload.UploadTaskQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

//When pressing the button, the activity takes MAX_TIMES nr of pictures with FREQUENCY ms frequency.
public class CameraActivity extends Activity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static final String TAG = "DataCollect:CameraActivity";
	
	private static final int MAX_TIMES = 1;
	private static final int FREQUENCY = 1000;

	private CameraPreview mPreview;
	private Camera mCamera;
	
	private Timer timer = new Timer();
	private int times = 0;
	private TimerTask timerTask = new TimerTask() {          
        @Override
        public void run() {
        	TimerMethod();
        }
	};
	
	private Object signalObject = new Object();
	private boolean imageInProgress = false;
	
	private UploadTaskQueue queue = UploadTaskQueue.instance(this);

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile != null) {	
				
				FileOutputStream fos = null;
				try {
					//can be written to memory too
					fos = new FileOutputStream(pictureFile);
					fos.write(data);					
				} catch (FileNotFoundException e) {
					Log.d(TAG, "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, "Error accessing file: " + e.getMessage());
				} finally {
					try {
						if (fos!=null) { fos.close(); }
					} catch (IOException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			} else {
				Log.d(TAG, "Error creating media file, check storage permissions");
				return;
			}
			
			Log.d(TAG, String.format("Image number %d captured", times));
			CameraActivity.this.queue.add(new ImageUploadTask(pictureFile));
			
			//need to start preview to make another picture
			if (times < MAX_TIMES)
				mCamera.startPreview();
			imageInProgress = false;
			//notifying the other thread to continue
			synchronized (signalObject){
				signalObject.notify();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		//Log.d(TAG, "onCreate called");

		if (this.checkCameraHardware()) {
			mCamera = getCameraInstance();
			mPreview = new CameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);

			Button captureButton = (Button) findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Scheduling timerTask for every " + FREQUENCY/1000 + " seconds.");
				    timer.schedule(timerTask, 0, FREQUENCY);
				}
			});
		} else {
			Log.d(TAG, "No camera.");
		}
	}
	
	private void TimerMethod() {

		//waiting for the other thread to complete the picture if it's in progress
		if (imageInProgress){
			synchronized (signalObject){
				try {
					signalObject.wait();
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}
		
		if (times >= MAX_TIMES){
			Log.d(TAG, "Reached " + MAX_TIMES + " times, cancelling task");
			mCamera.stopPreview();
			mCamera.release();
			mPreview.removePreview();
			timerTask.cancel();
			return;
		}
		times++;
		imageInProgress = true;
		Log.d(TAG, String.format("Running TimerMethod for %d time.", times));
		// get an image from the camera
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//mPreview for the first param gives shutter events
				if (mCamera!=null){
					mCamera.takePicture(null, null, mPicture);
				}
			}
		});
	}

	/** Check if this device has a camera */
	public boolean checkCameraHardware() {
		if (this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
			// Camera.getNumberOfCameras(); api level 9
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return c;
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"DistributedAlgosCamera");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("DistributedAlgosCamera", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	protected void onPause() {
		//Log.d(TAG, "onPause called");
		super.onPause();
		// releaseMediaRecorder(); 
		releaseCamera(); // release the camera immediately on pause event
		mPreview.removePreview();
		//cancelling the task
		timerTask.cancel();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//Log.d(TAG, "onResume called");
		this.openCamera();
		mPreview.initPreview();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		//Log.d(TAG, "onDestroy called");
		releaseCamera();
		mPreview.removePreview();
		
		timer.cancel();
	}
	
	private void openCamera(){
		if (mCamera == null){
			mCamera = getCameraInstance();
			mPreview.setCamera(mCamera);
		}
	}

	private void releaseCamera() {
		Log.d(TAG, "releaseCamera called");
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
			mPreview.setCamera(null);
		}
	}
}
