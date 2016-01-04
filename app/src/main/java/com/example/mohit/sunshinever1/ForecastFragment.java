package com.example.mohit.sunshinever1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
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
import android.widget.Toast;

import com.example.mohit.sunshinever1.listeners.Updatable;
import com.example.mohit.sunshinever1.webservices.FetchWeatherAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements Updatable {
    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order to handle menu events for this fragments.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                FetchWeatherAsync weatherTask = new FetchWeatherAsync();
                weatherTask.updatableObject = this;
                weatherTask.execute("560034");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        String[] fakeData = {
                "Today - Sunny-88/63",
                "Tommorow - Foggy-70/46",
                "Weds - Cloudy-72/63",
                "Thurs - Rainy-64/51",
                "Fri - Foggy-70/46",
                "Sat - Sunny-76/68"
        };
        ArrayList<String> weekForecast = new ArrayList<>(Arrays.asList(fakeData));

        //Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (Like our dummy forecast data)
        // use it to populate the ListView it's attached to.
        //Initialize the adapter i.e ArrayAdapter as our data is in ArrayList
        mForecastAdapter = new ArrayAdapter<String>(
                //The current context (this fragment's parent activity)
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                //ID of the textview, to populate
                R.id.list_item_forecast_textview,
                //Forecast data
                weekForecast);

        //Get the reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence tempString = ((TextView) view).getText();

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));

                TextView tv =(TextView) layout.findViewById(R.id.toast_text);
                tv.setText(tempString);

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        });

        return rootView;
    }

    @Override
    /**
     * Handler code for the event, when downloading of weather completes in Worker Thread.
     */
    public void onWeatherUpdate(List<String> weatherData) {
        mForecastAdapter.clear();
        for (String s : weatherData) {
            mForecastAdapter.add(s);
        }
    }

}

