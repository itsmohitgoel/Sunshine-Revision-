package com.example.mohit.sunshinever1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohit.sunshinever1.R;

/**
 * A placeholder fragment containing a simple view.
 *
 * Created by Mohit on 26-03-2016.
 */
public  class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intentContainer = getActivity().getIntent();
        CharSequence tempString = intentContainer.getCharSequenceExtra(Intent.EXTRA_TEXT);

        TextView textViewTitle = (TextView) rootView.findViewById(R.id.textview_detail_text);
        textViewTitle.setText(tempString);
        return rootView;
    }
}