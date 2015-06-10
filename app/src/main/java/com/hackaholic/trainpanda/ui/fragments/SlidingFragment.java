package com.hackaholic.trainpanda.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.custom.ComplexPreferences;

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
	private FragmentTabHost mTabHost;

	public SlidingFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_sliding, container, false);

		mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
		//mTabHost.setup(getActivity(), getActivity().getSupportFragmentManager(), android.R.id.tabcontent);
		mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

		mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Food",null), RestaurantFragmentFilter.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Hotel", null), HotelBookingWithStationCodeFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Info", null), WeatherFragment.class, null);

		return rootView;
	}


}
