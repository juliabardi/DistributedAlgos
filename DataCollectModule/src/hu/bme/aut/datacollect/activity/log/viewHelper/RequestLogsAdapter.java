package hu.bme.aut.datacollect.activity.log.viewHelper;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.utils.ViewDataUtils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * RequestList helper.
 * @author Eva Pataji
 *
 */
public class RequestLogsAdapter extends ArrayAdapter<RequestLogData> {
	private Context mContext;
	private List<RequestLogData> mRequests;

	public RequestLogsAdapter(Context context, List<RequestLogData> objects) {
		super(context, R.layout.log_requestrow, objects);
		
		mContext =context;
		mRequests = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RequestLogData request = mRequests.get(position);
		 LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.log_requestrow, parent, false);
	    ((TextView) rowView.findViewById(R.id.textViewName)).setText(request.getOfferName());
	    ((TextView) rowView.findViewById(R.id.textViewDate)).setText(ViewDataUtils.getDateInString(request.getRequestReceived()));
	    ImageView image = (ImageView)rowView.findViewById(R.id.imageViewRec);
	    if(request.getPeriodic()){
	    	image.setVisibility(View.VISIBLE);
	    }
	    else if(request.getStatusCode()!=null){
	    	if(request.getStatusCode().equals("200")){
	    		image.setImageResource(R.drawable.ok);
	    	}else{
	    		image.setImageResource(R.drawable.error);
	    	}
	    	image.setVisibility(View.VISIBLE);
	    }
	    return rowView;		
	}
}
