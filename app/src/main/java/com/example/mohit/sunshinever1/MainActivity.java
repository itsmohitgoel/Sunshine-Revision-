package com.example.mohit.sunshinever1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(intentSettingsActivity);
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationMap();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationMap() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Uri baseUri= Uri.parse("geo:0,0?");
        Uri.Builder builder = baseUri.buildUpon();
        Uri finalUri = builder.appendQueryParameter("q", location).build();

        Intent intentMap = new Intent(Intent.ACTION_VIEW);
        intentMap.setData(finalUri);
        if (intentMap.resolveActivity(getPackageManager()) != null) {
            startActivity(intentMap);
        }else {
            Log.d(LOG_TAG, "couldn't call " + location + ", no receiving apps installed");
        }

    }
}
