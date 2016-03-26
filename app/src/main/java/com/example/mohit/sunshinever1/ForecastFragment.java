package com.example.mohit.sunshinever1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mohit.sunshinever1.listeners.Updatable;
import com.example.mohit.sunshinever1.webservices.FetchWeatherAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements Updatable{
    ArrayAdapter<String> mForecastAdapter;
    private final static String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.
        String[] data = {"Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"};
        List<String> weekForecast = new ArrayList<>(Arrays.asList(data));

        //Create an arrayAdapter;
        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get the ref to listview widget, and add adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence tempString = ((TextView) view).getText();

                Intent intentDetailActivity = new Intent(getActivity(), DetailActivity.class);
                intentDetailActivity.putExtra(Intent.EXTRA_TEXT, tempString);
                startActivity(intentDetailActivity);
            }
        });

        //finally return the fragment rootview
        return rootView;
    }

    @Override
    /**
     * Handler code, to update the adapter's weather data
     * whenever its being downloaded in worker thread.
     */
    public void onWeatherUpdate(List<String> weatherData) {
        mForecastAdapter.clear();
        for (String s :weatherData) {
            mForecastAdapter.add(s);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    /**
     * Execute async task to re-download the weather data
     * as per the user defined location in settings menu.
     */
    private void updateWeather() {
        FetchWeatherAsync weatherTask = new FetchWeatherAsync(getActivity());
        weatherTask.updatableObject = this;
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationCode = sharedPreference.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        weatherTask.execute(locationCode);
    }
}
