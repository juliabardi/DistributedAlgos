package hu.bme.aut.datacollect.imageupload;

import android.content.Context;
import android.content.Intent;

import com.squareup.tape.InMemoryObjectQueue;
import com.squareup.tape.TaskQueue;

public class ImageUploadTaskQueue extends TaskQueue<ImageUploadTask> {
	
	private static ImageUploadTaskQueue instance;
	
	public static ImageUploadTaskQueue instance(){
		if (instance == null){
			instance = new ImageUploadTaskQueue();
		}
		return instance;
	}
	
	private ImageUploadTaskQueue() {
		super(new InMemoryObjectQueue<ImageUploadTask>());
	}
	
	public void startService(Context context) {
		context.startService(new Intent(context, ImageUploadTaskService.class));
	}

	@Override
	public void add(ImageUploadTask entry) {
		super.add(entry);
		//startService();
	}

	@Override
	public void remove() {
		super.remove();
	}

}
