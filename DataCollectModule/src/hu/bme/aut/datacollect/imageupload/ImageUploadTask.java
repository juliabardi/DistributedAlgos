package hu.bme.aut.datacollect.imageupload;

import java.util.Random;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.tape.Task;

public class ImageUploadTask implements Task<ImageUploadTask.Callback> {

	private static final long serialVersionUID = -8650032781289859212L;

	private static final String TAG = "Tape:ImageUploadTask";
	private static final Handler MAIN_THREAD = new Handler(
			Looper.getMainLooper());
	private Uri fileUri;

	public interface Callback {
		void onSuccess(String url);

		void onFailure();
	}

	public ImageUploadTask(Uri file) {
		this.fileUri = file;
	}

	@Override
	public void execute(final Callback callback) {

		new Thread(new Runnable() {
			@Override
			public void run() {

				// TODO upload to the server
				

				// if success
				if (new Random().nextBoolean()) {
					Log.i(TAG, "Upload success!");
					
					MAIN_THREAD.post(new Runnable() {
						@Override
						public void run() {
							callback.onSuccess("url");
						}
					});
				} else {
					Log.i(TAG, "Upload failed :(");

					// Get back to the main thread before invoking a callback.
					MAIN_THREAD.post(new Runnable() {
						@Override
						public void run() {
							callback.onFailure();
						}
					});
				}
			}
		}).start();
	}

}
