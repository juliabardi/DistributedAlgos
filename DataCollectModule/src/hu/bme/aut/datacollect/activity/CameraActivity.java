package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.upload.CameraPreview;
import hu.bme.aut.datacollect.upload.ImageUploadTask;
import hu.bme.aut.datacollect.upload.ImageUploadTaskQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
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
	
//	private static int MAX_TIMES = 1;
//	private static int FREQUENCY = 1000;

	private CameraPreview mPreview;
	private Camera mCamera;
	
	private Timer timer;
	private int times = 0;
	
	private TimerTask timerTask;

	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				
				//if ImageData got disabled, don't make any more pictures
				if (!DataCollectService.isDataTypeEnabled(CameraActivity.this, DataCollectService.IMAGE)){
					Log.d(TAG, "ImageData has been disabled by the user!");
					cancelNotification();
					finish();
					return;
				}
				
				// waiting for the other thread to complete the picture if it's
				// in progress
				if (imageInProgress) {
					synchronized (signalObject) {
						try {
							signalObject.wait();
						} catch (InterruptedException e) {
							Log.e(TAG, e.getMessage());
						}
					}
				}

				if (times >= max_times) {
					Log.d(TAG, "Reached " + max_times
							+ " times, cancelling task");
					if (!released)
						mCamera.stopPreview();
					mCamera.release();
					mPreview.removePreview();
					timerTask.cancel();

					// images are ready, we can remove the notif and finish the
					// activity
					cancelNotification();
					finish();

					return;
				}
				times++;
				imageInProgress = true;
				Log.d(TAG, String.format("Running TimerMethod for %d time.",
						times));
				// get an image from the camera
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// mPreview for the first param gives shutter events
						if (mCamera != null) {
							mCamera.takePicture(null, null, mPicture);
						}
					}
				});
			}
		};
	}
	
	private Object signalObject = new Object();
	private boolean imageInProgress = false;
	
	private ImageUploadTaskQueue imageQueue = ImageUploadTaskQueue.instance(this);
	
	private String address = null;
	private String reqId;
	private int width = 0;
	private int height = 0;
	
	//seconds
	private int recurrence = 1;
	private int max_times = 1;
	
	private boolean released = false;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			long timestamp = Calendar.getInstance().getTimeInMillis();

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
			CameraActivity.this.imageQueue.add(new ImageUploadTask(pictureFile, address, reqId, timestamp));
			
			//need to start preview to make another picture
			if (times < max_times)
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
		Log.d(TAG, "onCreate called");
		this.init(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent called");
		if (timerTask != null){
			timerTask.cancel();
		}
		timer.cancel();
		timer.purge();
		this.init(intent);
	}

	private void init(Intent intent){
		
		if (intent != null){
			this.address = intent.getStringExtra("address");
			this.reqId = intent.getStringExtra("reqId");
			this.width = intent.getIntExtra("width", 0);
			this.height = intent.getIntExtra("height", 0);
			this.setRecurrenceMaxTimes(intent);
			Log.d(TAG, String.format("init called, params: times:%d, recurrence:%d, width: %d, height: %d", max_times, recurrence, width, height));
		}

		if (this.checkCameraHardware()) {
			mCamera = getCameraInstance();			
			if (mCamera != null){
				released = false;
				this.setPictureSize();
				
				mPreview = new CameraPreview(this, mCamera);
				FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				preview.addView(mPreview);
	
				final Button captureButton = (Button) findViewById(R.id.button_capture);
				captureButton.setEnabled(true);
				captureButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(TAG, "Scheduling timerTask for every " + recurrence + " seconds.");
						timer = new Timer();
						timerTask = createTimerTask();
					    timer.scheduleAtFixedRate(timerTask, 0, recurrence*1000);
					    captureButton.setEnabled(false);
					}
				});
			}
		} else {
			Log.d(TAG, "No camera.");
		}
	}

	private void setPictureSize(){
		
		if (this.pictureSizeExists()){
			mCamera.getParameters().setPictureSize(width, height);
		}
		else {
			List<Size> supported = mCamera.getParameters().getSupportedPictureSizes();
			Size min = supported.get(supported.size()-1);
			mCamera.getParameters().setPictureSize(min.width, min.height);
		}
	}
	
	//returning true if the given picture size (width, height) is supported, else false
	private boolean pictureSizeExists(){
		
		if (width != 0 && height != 0){
			
			List<Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
			for (Size size : sizes){
				if (size.width == width && size.height == height){
					return true;
				}
			}
		}
		return false;
	}
	
	private void setRecurrenceMaxTimes(Intent intent){
		if (intent != null){
			
			if (intent.hasExtra("recurrence") && intent.hasExtra("times")){
				this.max_times = intent.getIntExtra("times", 1);
				this.recurrence = intent.getIntExtra("recurrence", 1);
			}
			if (intent.hasExtra("recurrence") && !intent.hasExtra("times")){
				this.recurrence = intent.getIntExtra("recurrence", 1);
				this.max_times = 10;  //restricting max times to 10 because of performance problems
			}
			if (!intent.hasExtra("recurrence") && intent.hasExtra("times")){
				this.max_times = intent.getIntExtra("times", 1);
			}
		}
	}
	
	private void cancelNotification(){
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(reqId, DataCollectService.IMAGE_NOTIF_ID);
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
				Log.d(TAG, "failed to create directory");
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
		timer.cancel();
		if (timerTask != null){
			timerTask.cancel();
		}
		cancelNotification();
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
		if (timerTask != null){
			timerTask.cancel();
		}
		timer.cancel();
		cancelNotification();
	}
	
	private void openCamera(){
		if (mCamera == null){
			mCamera = getCameraInstance();
			mPreview.setCamera(mCamera);
			released = false;
		}
	}

	private void releaseCamera() {
		Log.d(TAG, "releaseCamera called");
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
			mPreview.setCamera(null);
			released = true;
		}
	}
}
