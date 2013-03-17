package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.R;
import hu.bme.aut.datacollect.db.DataCollectDao;
import hu.bme.aut.datacollect.entity.Acceleration;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DetailsActivity extends Activity {

	private final DataCollectDao dao = DataCollectDao.getInstance(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.detail_view);
		this.loadTableData();
	}

	protected void loadTableData(){
		
		TableLayout table = (TableLayout)findViewById(R.id.table);
		
		List<Acceleration> list = dao.getAllAccelerations();
		for (Acceleration acc : list) {
			TableRow row = new TableRow(this);
			TextView tv = new TextView(this);
			tv.setText(String.format(
					"accX:%f, accY:%f, accZ:%f", acc.getAccX(), acc.getAccY(),
					acc.getAccZ()));
			row.addView(tv);
			table.addView(row);
		}
	}
}
