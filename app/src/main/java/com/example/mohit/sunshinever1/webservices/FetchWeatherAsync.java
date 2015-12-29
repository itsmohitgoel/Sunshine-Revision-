package com.example.mohit.sunshinever1.webservices;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.example.mohit.sunshinever1.listeners.Updatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Class to retrieve weather data from the OpenWeatherMap api
 *
 * Created by Mohit on 29-12-2015.
 */
public class FetchWeatherAsync extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchWeatherAsync.class.getSimpleName();
    public Updatable updatableObject;

    /* The date/time conversion code is going to be moved  outside the asynctask later,
     * so for  convenience I'm breaking it out into its own method now.
     */
    private String getReadableDateString(long time) {
        // Because the api returns the UNIX timestamp(measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/low for  presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the string representing the complete forecast in JSON format and
     * pull out the data we need to construct the strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy: constructor takes the JSON string and convert it
     * into an object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
        // These are the names of JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this
        // data properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're goint to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format " Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather" , which is 1-element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp". Try not to name the variabes
            // "temp" when working with temperatures. It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + "-" + description + "-" + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }
        return resultStrs;
    }

    @Override
    protected String[] doInBackground(String... params) {
        // If there's no zip code, there's nothing to look up. Verify size of params.
        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside try/catch block
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON  response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        String numDays = "7";
        String appID = "1d8608cb722da1f8f1f18011bd298fe0";

        try {
            // Construct the URL for OpenWeatherMap query.
            // Possible Parameter's are availaible at OWM's forecast API page.
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APP_ID = "appid";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, numDays)
                    .appendQueryParameter(APP_ID, appID)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create a request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a new line isn't necessary(it won't affec parsing)
                // but it makes debugging a *lot* easier if we print out the completed
                // buffer for debugging;
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing;
                return null;
            }
            forecastJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        try {
            return getWeatherDataFromJson(forecastJsonStr, Integer.parseInt(numDays));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        // Notify the fragment adapter about the new downloaded weather data.
        updatableObject.onWeatherUpdate(Arrays.asList(strings));

    }
}
