package ca.cumulonimbus.barometernetwork;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ca.cumulonimbus.barometernetwork.PressureNetApplication.TrackerName;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SearchLocationsActivity extends ListActivity {

	PnDb pn;
	ListAdapter adapter = null;
	Cursor cursor = null;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent resultIntent = new Intent();
		resultIntent.putExtra("location_id", id);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	@Override
	protected void onStart() {
		// Get tracker.
		Tracker t = ((PressureNetApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("Search Locations");

		// Send a screen view.
		t.send(new HitBuilders.ScreenViewBuilder().build());
		super.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_locations);

		pn = new PnDb(getApplicationContext());
		pn.open();
		cursor = pn.fetchAllLocations();

		if (cursor.getCount() == 0) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("location_id", -2L);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}

		startManagingCursor(cursor);

		adapter = new SimpleCursorAdapter(this, R.layout.location_list_item,
				cursor, new String[] { PnDb.KEY_SEARCH_TEXT },
				new int[] { R.id.textLocationName });
		setListAdapter(adapter);

		this.getListView().setLongClickable(true);
		this.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent,
							View v, int position, long id) {
						Intent intent = new Intent(getApplicationContext(),
								EditLocationActivity.class);
						intent.putExtra("rowId", id);
						startActivity(intent);
						return true;
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (cursor != null) {
			cursor.requery();
		}
	}

	@Override
	protected void onDestroy() {
		if (pn != null) {
			pn.close();
		}
		super.onStop();
	}

}
