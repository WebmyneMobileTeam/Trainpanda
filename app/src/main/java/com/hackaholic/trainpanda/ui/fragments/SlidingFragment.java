package com.hackaholic.trainpanda.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.utility.CustomDialogBoxEditPNR;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import Model.PNR;

public class SlidingFragment extends Fragment {
	private TabHost mTabHost;

	public SlidingFragment(){}
	String stName;
	boolean isHotel;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args  != null && args.containsKey("stName")){
			stName= args.getString("stName");

		}

		/*if (args  != null && args.containsKey("isHotel")){
			String ishot = args.getString("isHotel");

					if(ishot.equalsIgnoreCase("true")){
						isHotel = true;
					}else{
						isHotel = false;
					}
		}*/

	}





	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		TextView title = (TextView)getActivity(). findViewById(R.id.lk_profile_header_textview);
		title.setText(stName);


		ImageView imgToolbarOption = (ImageView) getActivity().findViewById(R.id.imgToolbarOption);
		imgToolbarOption.setVisibility(View.VISIBLE);

		imgToolbarOption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(),"Not working",Toast.LENGTH_SHORT).show();
			}
		});



		View rootView = inflater.inflate(R.layout.activity_sliding, container, false);

		mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);

		mTabHost.setup();

		//mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);


		setupTab(new TextView(getActivity()), "Tab 1");

		setupTab(new TextView(getActivity()), "Tab 2");

		setupTab(new TextView(getActivity()), "Tab 3");


		/*if(isHotel) {
			mTabHost.setCurrentTabByTag("Tab 1");
		}else{
			mTabHost.setCurrentTabByTag("Tab 2");
		}*/
		mTabHost.setCurrentTabByTag("Tab 1");


		if(mTabHost.getCurrentTabTag().equalsIgnoreCase("Tab 1")){
			RestaurantFragmentFilter fragment = new RestaurantFragmentFilter();
			FragmentManager fragmentManager11 = getFragmentManager();
			fragmentManager11.beginTransaction().replace(android.R.id.tabcontent, fragment).commit();
		}



		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String s) {

				switch (s) {

					case "Tab 1":
						RestaurantFragmentFilter fragment = new RestaurantFragmentFilter();
						FragmentManager fragmentManager11 = getFragmentManager();
						fragmentManager11.beginTransaction().replace(android.R.id.tabcontent, fragment).commit();

						break;
					case "Tab 2":
						HotelBookingWithStationCodeFragment fragment2 = new HotelBookingWithStationCodeFragment();
						FragmentManager fragmentManager2 = getFragmentManager();
						fragmentManager2.beginTransaction().replace(android.R.id.tabcontent, fragment2).commit();
						break;
					case "Tab 3":
						WeatherFragment fragment3 = new WeatherFragment();
						FragmentManager fragmentManager3 = getFragmentManager();
						fragmentManager3.beginTransaction().replace(android.R.id.tabcontent, fragment3).commit();
						break;
				}
			}
		});

		return rootView;
	}

	private void setupTab(final View view, final String tag) {


		View tabview = createTabView(mTabHost.getContext(), tag);



		TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory() {

			public View createTabContent(String tag) {return view;}

		});

		mTabHost.addTab(setContent);

	}


	private static View createTabView(final Context context, final String text) {

		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);

		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		ImageView imgIcon= (ImageView) view.findViewById(R.id.imgIcon);
		switch (text) {

			case "Tab 1":
				imgIcon.setBackgroundResource(R.drawable.icon_food);
				tv.setText("Food");
				break;
			case "Tab 2":
				imgIcon.setBackgroundResource(R.drawable.icon_hotel);
				tv.setText("Hotel");
				break;
			case "Tab 3":
				imgIcon.setBackgroundResource(R.drawable.icon_info);
				tv.setText("Info");
				break;
		}


		return view;

	}



}
