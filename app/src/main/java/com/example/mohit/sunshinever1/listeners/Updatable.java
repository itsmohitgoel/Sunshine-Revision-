package com.example.mohit.sunshinever1.listeners;

import java.util.List;

/**
 * Interface with callback methods for communication between AsyncTask and Fragments.
 *
 * Created by Mohit on 29-12-2015.
 */
public interface Updatable{

        /**
         * Called on AsyncTask completes
         *
         * @param weatherData
         */
    public abstract void onWeatherUpdate(List<String> weatherData);
}
