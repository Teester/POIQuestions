package com.teester.whatsnearby.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teester.whatsnearby.R;
import com.teester.whatsnearby.data.PreferenceList;
import com.teester.whatsnearby.data.source.Preferences;
import com.teester.whatsnearby.data.source.SourceContract;

import java.util.Locale;

public class FragmentDebug extends Fragment implements MainActivityContract.DebugView, SharedPreferences.OnSharedPreferenceChangeListener {

	private TextView lastQueryTime;
	private TextView lastNotificationTime;
	private TextView lastQuery;
	private TextView accuracy;
	private TextView querydistance;
	private TextView checkdistance;
	private TextView lastLocation;
	private MainActivityContract.DebugPresenter debugPresenter;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
		SourceContract.Preferences preferences = new Preferences(getContext());
		debugPresenter = new DebugPresenter(this, preferences);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_debug, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.lastQueryTime = view.findViewById(R.id.debug_last_overpass_query_value);
		this.lastNotificationTime = view.findViewById(R.id.debug_last_notification_value);
		this.lastQuery = view.findViewById(R.id.debug_last_overpass_query_result_value);
		this.accuracy = view.findViewById(R.id.debug_accuracy_value);
		this.querydistance = view.findViewById(R.id.debug_distance_since_last_query_value);
		this.checkdistance = view.findViewById(R.id.debug_distance_since_last_location_check_value);
		this.lastLocation = view.findViewById(R.id.debug_most_recent_location_value);

		debugPresenter.getDetails();
	}

	@Override
	public void onPause() {
		super.onPause();
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void setLastQueryTime(String time, int color) {
		this.lastQueryTime.setText(time);
		this.lastQueryTime.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), color));
	}

	@Override
	public void setLastNotificationTime(String notificationTime, int color) {
		this.lastNotificationTime.setText(notificationTime);
		this.lastNotificationTime.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), color));
	}

	@Override
	public void setLastQuery(String queryTime) {
		this.lastQuery.setText(queryTime);
	}

	@Override
	public void setAccuracy(String accuracy, int color) {
		this.accuracy.setText(accuracy);
		this.accuracy.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), color));
	}

	@Override
	public void setQuerydistance(String querydistance, int color) {
		this.querydistance.setText(querydistance);
		this.querydistance.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), color));
	}

	@Override
	public void setCheckdistance(String queryTimeSince, int color) {
		this.checkdistance.setText(queryTimeSince);
		this.checkdistance.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), color));
	}

	@Override
	public void setLocation(double latitude, double longitude) {
		this.lastLocation.setText(String.format(Locale.getDefault(), "%f, %f", latitude, longitude));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		switch (s) {
			case PreferenceList.LATITUDE:
			case PreferenceList.LONGITUDE:
			case PreferenceList.DISTANCE_TO_LAST_LOCATION:
			case PreferenceList.LAST_QUERY_TIME:
			case PreferenceList.LAST_NOTIFICATION_TIME:
			case PreferenceList.DISTANCE_TO_LAST_QUERY:
			case PreferenceList.LAST_QUERY:
			case PreferenceList.LOCATION_ACCURACY:
				debugPresenter.getDetails();
				break;
			default:
				break;
		}
	}
}
