package hu.bme.aut.datacollect.activity.log.viewHelper;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.communication.entity.ResponseLogData;
import hu.bme.aut.datacollect.activity.R;
import hu.bme.aut.datacollect.activity.log.utils.ViewDataUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ResponseList helper.
 * @author Eva
 *
 */
public class ResponseLogsAdapter extends ArrayAdapter<ResponseLogData> {
	private Context mContext;
	private List<ResponseLogData> mResponses;

	public ResponseLogsAdapter(Context context, List<ResponseLogData> objects) {
		super(context, R.layout.log_tab_responserow, objects);
		
		mContext =context;
		mResponses = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResponseLogData response = mResponses.get(position);
		 LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.log_tab_responserow, parent, false);
	    ((TextView) rowView.findViewById(R.id.textViewResponseSent)).setText(ViewDataUtils.getDateInString(
	    		response.getResponseSent()));
	    ((TextView) rowView.findViewById(R.id.textViewAnswerReceived)).setText(ViewDataUtils.getDateInString(
	    		response.getAnswerReceived()));
	    ((TextView) rowView.findViewById(R.id.textViewStatusCode)).setText(ViewDataUtils.getStringValue(
	    		response.getStatusCode()));
	    ((TextView) rowView.findViewById(R.id.textViewAnswerParams)).setText(ViewDataUtils.getStringValue(
	    		response.getAnswerParams()));
	    
	    return rowView;		
	}
	
	
}
