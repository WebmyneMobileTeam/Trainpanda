package com.hackaholic.trainpanda.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.adapter.ListAdapter;
import com.hackaholic.trainpanda.ui.fragments.BookHotel;
import com.hackaholic.trainpanda.ui.fragments.FareEnquiry;
import com.hackaholic.trainpanda.ui.fragments.MainFragment;
import com.hackaholic.trainpanda.ui.fragments.MyFoodOrder;
import com.hackaholic.trainpanda.ui.fragments.MySearchedPNR;
import com.hackaholic.trainpanda.ui.fragments.OrderFood;
import com.hackaholic.trainpanda.ui.fragments.PNRFragment;
import com.hackaholic.trainpanda.ui.fragments.RunningStatus;
import com.hackaholic.trainpanda.ui.fragments.SearchTrain;
import com.hackaholic.trainpanda.ui.fragments.SeatAvailability;
import com.hackaholic.trainpanda.ui.fragments.StationInfo;
import com.hackaholic.trainpanda.ui.fragments.TrainArraiving;
import com.hackaholic.trainpanda.utils.SlidingMenuLayout;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import android.util.Log;

public class RestrauntDetail extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail);

        String stCode = getIntent().getStringExtra("stCode");

        Toast.makeText(RestrauntDetail.this,"clicked "+stCode,Toast.LENGTH_SHORT).show();

    }


}
