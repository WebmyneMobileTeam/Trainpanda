package com.hackaholic.trainpanda.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ui.fragments.HotelBookingWithStationCodeFragment;
import com.hackaholic.trainpanda.ui.fragments.RestaurantFragmentFilter;
import com.hackaholic.trainpanda.ui.fragments.WeatherFragment;


public class SlidingActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Food", null), RestaurantFragmentFilter.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Hotel", null), HotelBookingWithStationCodeFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Info", null), WeatherFragment.class, null);
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * *//*
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}


	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());

	}



	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}*/

}
