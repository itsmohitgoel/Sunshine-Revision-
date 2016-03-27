package com.example.mohit.sunshinever1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 *
 * Created by Mohit on 26-03-2016.
 */
public  class DetailActivityFragment extends Fragment {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private CharSequence forecastString;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intentContainer = getActivity().getIntent();
        if (intentContainer != null && intentContainer.hasExtra(Intent.EXTRA_TEXT)) {
            forecastString = intentContainer.getCharSequenceExtra(Intent.EXTRA_TEXT);

            TextView textViewTitle = (TextView) rootView.findViewById(R.id.textview_detail_text);
            textViewTitle.setText(forecastString);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        Intent shareIntent = createForecastShareIntent();

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }else {
            Log.d(LOG_TAG, "Share Action Provider is null");
        }
    }

    @NonNull
    private Intent createForecastShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return shareIntent;
    }
}