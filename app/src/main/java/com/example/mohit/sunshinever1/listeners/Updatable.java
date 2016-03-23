package com.example.mohit.sunshinever1.listeners;

import java.util.List;

/**
 * Interface with callback methods for communication b/w AsyncTask
 * and fragments
 *
 * Created by Mohit on 23-03-2016.
 */
public interface Updatable {
    /**
     * Called mostly on Adapter object, when AsynchTask completes.
     * @param weatherData
     */
    public abstract void onWeatherUpdate(List<String> weatherData);
}
