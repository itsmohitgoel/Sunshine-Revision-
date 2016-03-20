package com.example.mohit.sunshinever1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> mForecastAdapter;
    private static String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
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

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=560102&mode=json&units=metric&cnt=7";
            String apiKey = "&appid=b1b15e88fa797225412429c1c50c122a";
            URL url = new URL(baseURL + apiKey);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            if (is == null) {
                return null;
            }
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            String line;
            StringBuffer bufferString = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                bufferString.append(line).append("\n");
            }

            if (bufferString.length() == 0) {
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error");
            return null;
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing BufferedReader");
                }
            }
        }
        //finally return the fragment rootview
        return rootView;
    }
}
	