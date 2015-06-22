package com.hackaholic.trainpanda.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.helpers.PrefUtils;

import Model.RecentOrder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class RatingsFragment extends Fragment
{
	private SharedPreferences sharedPreferences;
	RecentOrder recentOrder;
	RatingBar ratingBar;
	int RATING;
	TextView txtHotelName,txtStaionName,txtorderdate,txtAmount,txtSubmit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View row=inflater.inflate(R.layout.ratings_fragment, container,false);

		TextView title = (TextView) getActivity().findViewById(R.id.lk_profile_header_textview);
		title.setText("Feedback");

		ImageView imgToolbarOption = (ImageView) getActivity().findViewById(R.id.imgToolbarOption);
		imgToolbarOption.setVisibility(View.GONE);

		sharedPreferences = getActivity().getSharedPreferences("TrainPanda", getActivity().MODE_PRIVATE);

		initViews(row);

		//Setting that rating is completed for recent order
		//PrefUtils.setRecentOrder(getActivity(),false);

		recentOrder = PrefUtils.getRecentOrederObject(getActivity());


		Log.e("#### Restraunt ID",recentOrder.RestrauntID);
		Log.e("#### Restraunt Name",recentOrder.RestrauntName);
		Log.e("#### Station Name",recentOrder.stationName);
		Log.e("#### OrderDate",recentOrder.OrderDate);
		Log.e("#### OrderAmount ",recentOrder.OrderAmount);
		Log.e("#### OrderId ",recentOrder.OrderId);


		txtHotelName.setText(recentOrder.RestrauntName);
		txtorderdate.setText(recentOrder.OrderDate);
		txtStaionName.setText("Station - "+recentOrder.stationName);
		txtAmount.setText("INR "+recentOrder.OrderAmount);


		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
				RATING = (int) v;
			}
		});


		txtSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (RATING == 0) {
					Toast.makeText(getActivity(), "Please give some rating !!!", Toast.LENGTH_SHORT).show();
				} else {
					processGIVERATING();
				}
			}
		});

		return row;
	}

	private  void processGIVERATING(){

		try {

			JSONObject jsonObject = new JSONObject();

			jsonObject.put("restaurantId",recentOrder.RestrauntID);
			jsonObject.put("userId", sharedPreferences.getString("customer_id", "").trim());
			jsonObject.put("orderId",recentOrder.OrderId );
			jsonObject.put("feedback", "test");
			jsonObject.put("rating",RATING);


			Log.e("FINAL rating JSON : ", "" + jsonObject);


			JSONPost json1 = new JSONPost();
			json1.POST(getActivity(), "http://admin.trainpanda.com/api/restaurantRatings", jsonObject.toString(), "Submitting your rating...");
			json1.setPostResponseListener(new POSTResponseListener() {
				@Override
				public String onPost(String msg) {

					Log.e("rating res", msg);

					//Setting that rating is completed for recent order
					PrefUtils.setRecentOrder(getActivity(),false);

					Toast.makeText(getActivity(), "Thank you for submitting your rating. We value your feedback.", Toast.LENGTH_SHORT).show();

					PNRFragment fragment = new PNRFragment();
					FragmentManager fragmentManager22 = getFragmentManager();
					fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();

					return null;
				}

				@Override
				public void onPreExecute() {

				}

				@Override
				public void onBackground() {

				}
			});

		} catch (Exception e) {
			Log.e("--- exc",e.toString());
			e.printStackTrace();
		}


	}



	private void initViews(View row){


		ratingBar= (RatingBar)row.findViewById(R.id.ratingBar);

		txtHotelName = (TextView)row.findViewById(R.id.txtHotelName);
		txtorderdate= (TextView)row.findViewById(R.id.txtorderdate);
		txtStaionName= (TextView)row.findViewById(R.id.txtStaionName);
		txtAmount= (TextView)row.findViewById(R.id.txtAmount);
		txtSubmit= (TextView)row.findViewById(R.id.txtSubmit);
	}

	//end of mian class
}
