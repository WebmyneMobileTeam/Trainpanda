package com.hackaholic.trainpanda.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackaholic.trainpanda.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunningStatus extends Fragment {


    public RunningStatus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_running_status, container, false);
    }


}
