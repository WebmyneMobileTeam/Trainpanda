package com.hackaholic.trainpanda.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.MainHotel;
import Model.Restraunt;

public class HotelBookingWithStationCodeFragment extends Fragment implements OnClickListener
{

	private TextView hotel_fragment_filter_tv_go,txtNoData;
	private ProgressBar hotel_fragment_filter_progressBar;
	private AutoCompleteTextView hotel_fragment_filter_actv_source_station;
	private ListView hotel_fragment_filter_listview;
	MainHotel curr_hotel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View row=inflater.inflate(R.layout.hotel_booking_with_station_code, container,false);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		initializeACTV(row);

		getHotelsDetails();
		return row;
	}

	private void getHotelsDetails(){

		String stCode = PrefUtils.getCurrentStationCode(getActivity());
		Log.e("stCode", stCode);

		new GoTask().execute(
				new String[]
						{
								stCode
						}
		);
	}


	private void initializeACTV(View row)
	{
		txtNoData=(TextView)row.findViewById(R.id.txtNoData);
		hotel_fragment_filter_listview=(ListView)row.findViewById(R.id.hotel_fragment_filter_listview);
		//Go Button
		hotel_fragment_filter_tv_go=(TextView)row.findViewById(R.id.hotel_fragment_filter_tv_go);
		hotel_fragment_filter_tv_go.setOnClickListener(this);

		hotel_fragment_filter_progressBar = (ProgressBar) row.findViewById(R.id.hotel_fragment_filter_progressBar);

		//AutoCompleteTextview for source stations
		hotel_fragment_filter_actv_source_station=(AutoCompleteTextView)row.findViewById(R.id.hotel_fragment_filter_actv_source_station);
		hotel_fragment_filter_actv_source_station.setThreshold(1);
		hotel_fragment_filter_actv_source_station.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if(hotel_fragment_filter_actv_source_station.getText().toString().length()>2)
				{
					new ProgressTask().execute(hotel_fragment_filter_actv_source_station.getText().toString().trim());
				}
			}
		});


	}


	private class ProgressTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected void onPreExecute()
		{
			hotel_fragment_filter_progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params)
		{
			return hitServer(params[0]);
		}

		private String hitServer(String value)
		{
			String response=null;
			String url="http://api.railwayapi.com/suggest_station/name/"+value+"/apikey/"+getResources().getString(R.string.key1)+"/";
			try
			{
				ServiceHandler handler=new ServiceHandler();
				response=handler.makeServiceCall(url.replaceAll(" ","%20"), ServiceHandler.GET);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result)
		{
			hotel_fragment_filter_progressBar.setVisibility(View.GONE);

			if(result!=null)
			{
				//Toast.makeText(getActivity(), ""+result, Toast.LENGTH_LONG).show();
				Log.e("REsponse", "" + result);

				ArrayList<String> al_fullname=new ArrayList<String>();
				ArrayList<String> al_code=new ArrayList<String>();

				try
				{
					JSONObject jsonObject=new JSONObject(result);
					JSONArray jsonArray=jsonObject.getJSONArray("station");
					for(int i=0;i<jsonArray.length();i++)
					{
						JSONObject jsonObject2=jsonArray.getJSONObject(i);
						String fullname=jsonObject2.getString("fullname");
						String code=jsonObject2.getString("code");
						al_fullname.add(fullname);
						al_code.add(code);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				ArrayAdapter<String> adapter2=new ArrayAdapter<String>(getActivity(),
						R.layout.single_row_textview,R.id.signle_row_textview_tv,al_fullname);
				hotel_fragment_filter_actv_source_station.setAdapter(adapter2);
			}
			else
			{
				Toast.makeText(getActivity(), "Response Is null", Toast.LENGTH_SHORT).show();
			}
		}
	}


	private class GoTask extends AsyncTask<String,Void, String>
	{
		ProgressDialog dialog;
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			dialog=new ProgressDialog(getActivity());
			dialog.setCancelable(false);
			dialog.setMessage("Please Wait...!");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params)
		{
			//String station_code=getStationCode(getStationResponse(params[0]));

			String station_code = PrefUtils.getCurrentStationCode(getActivity());
			Log.e("Station Code : ", "" + station_code);

			String url= API.BASE_URL+"hotels?filter[where][stationCode]="+station_code.trim();
			String response=hitUrl(url);
			Log.e("response hotel : ", "" + response);
			return response;
		}


		private String hitUrl(String url)
		{
			String response=null;
			try
			{
				ServiceHandler handler=new ServiceHandler();
				response=handler.makeServiceCall(url, ServiceHandler.GET);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return response;
		}


		private String getStationCode(String response)
		{
			String station_code=null;
			try
			{
				JSONObject jsonObject=new JSONObject(response);
				JSONArray jsonArray=jsonObject.getJSONArray("station");
				station_code=jsonArray.getJSONObject(0).getString("code");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return station_code;
		}

		private String getStationResponse(String station_name)
		{
			String response=null;
			String url="http://api.railwayapi.com/suggest_station/name/"+station_name.replaceAll(" ","%20")+"/apikey/"+getResources().getString(R.string.key1)+"/";
			try
			{
				ServiceHandler handler=new ServiceHandler();
				response=handler.makeServiceCall(url, ServiceHandler.GET);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			if(result!=null)
			{
				try
				{
					JSONObject mainObject=new JSONObject();
					JSONArray jsonArray=new JSONArray(result);
					mainObject.put("Hotels",jsonArray);
					Log.e("final hotel list", mainObject.toString());

					curr_hotel  = new GsonBuilder().create().fromJson(mainObject.toString(), MainHotel.class);

				}
				catch(Exception e)
				{
					Log.e("exc ",e.toString());
					e.printStackTrace();
				}
			}
			else
			{
				printMessage("Server Error,Response is null...!");	
			}

			dialog.dismiss();

			try {
				if (curr_hotel.Hotels.size() == 0) {
					hotel_fragment_filter_listview.setVisibility(View.GONE);
					txtNoData.setVisibility(View.VISIBLE);
					txtNoData.setTypeface(PrefUtils.getTypeFace(getActivity()));
					txtNoData.setText("India is big! And we are trying hard to cover all stations across country. We will let you know as soon as we cover this station.");


				} else {

					txtNoData.setVisibility(View.GONE);
					hotel_fragment_filter_listview.setAdapter(new MyAdapter(getActivity(),curr_hotel));
				}
			}catch (NullPointerException e){
				hotel_fragment_filter_listview.setVisibility(View.GONE);
				txtNoData.setVisibility(View.VISIBLE);
				txtNoData.setTypeface(PrefUtils.getTypeFace(getActivity()));
				txtNoData.setText("India is big! And we are trying hard to cover all stations across country. We will let you know as soon as we cover this station.");

			}catch (Exception e){
				hotel_fragment_filter_listview.setVisibility(View.GONE);
				txtNoData.setVisibility(View.VISIBLE);
				txtNoData.setTypeface(PrefUtils.getTypeFace(getActivity()));
				txtNoData.setText("India is big! And we are trying hard to cover all stations across country. We will let you know as soon as we cover this station.");

			}




		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

		case R.id.hotel_fragment_filter_tv_go:

			if(!hotel_fragment_filter_actv_source_station.getText().toString().trim().equals(""))
			{
				new GoTask().execute(
						new String[]
								{
								hotel_fragment_filter_actv_source_station.getText().toString().trim(),
								}
						);
			}
			else
			{
				printMessage("Please fill train number...!");
			}
			break;

		default:
			break;
		}
	}

	private void printMessage(String msg)
	{
		Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
	}


	private class MyAdapter extends BaseAdapter
	{
		private Context context;
		private MainHotel valuesHotel;

		public MyAdapter(Context context,MainHotel values)
		{
			this.context=context;
			this.valuesHotel = values;
		}
		@Override
		public int getCount() 
		{
			return valuesHotel.Hotels.size();
		}
		@Override
		public Object getItem(int position)
		{
			return valuesHotel.Hotels.get(position);
		}
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View row=convertView;
			MyHolder holder;
			if(row==null)
			{
				holder=new MyHolder();
				LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				row=inflater.inflate(R.layout.single_row_hotel_booking, parent,false);

				holder.hotel_name=(TextView)row.findViewById(R.id.nameHotel);
				holder.mobile=(TextView)row.findViewById(R.id.mobile);
				holder.address=(TextView)row.findViewById(R.id.address);
				holder.priceRange=(TextView)row.findViewById(R.id.priceRange);
				holder.distance=(TextView)row.findViewById(R.id.distance);
				holder.imgHotel = (ImageView)row.findViewById(R.id.imgHotel);
				holder.tvRating=(TextView)row.findViewById(R.id.tvRating);


				holder.hotel_name.setTypeface(PrefUtils.getTypeFace(getActivity()));
				holder.tvRating.setTypeface(PrefUtils.getTypeFace(getActivity()));
				holder.mobile.setTypeface(PrefUtils.getTypeFace(getActivity()));
				holder.address.setTypeface(PrefUtils.getTypeFace(getActivity()));
				holder.priceRange.setTypeface(PrefUtils.getTypeFace(getActivity()));
				holder.distance.setTypeface(PrefUtils.getTypeFace(getActivity()));


				row.setTag(holder);
			}
			else
			{
				holder=(MyHolder) row.getTag();
			}
			holder.hotel_name.setText(valuesHotel.Hotels.get(position).name);
			holder.mobile.setText(""+valuesHotel.Hotels.get(position).mobileNo);

			holder.distance.setText(""+valuesHotel.Hotels.get(position).distanceFromStation+" Km from station");
			holder.address.setText(valuesHotel.Hotels.get(position).email);
			holder.priceRange.setText(""+valuesHotel.Hotels.get(position).priceRange+" INR");
			holder.tvRating.setText(""+valuesHotel.Hotels.get(position).hotelCategory);


			try {
				if (valuesHotel.Hotels.get(position).images.size() != 0) {
					Glide.with(context).load(API.BASE_IMAGE_URL + valuesHotel.Hotels.get(position).images.get(0).url).thumbnail(0.1f).into(holder.imgHotel);
				}
			}catch (Exception e){
					Log.e("exc null images",e.toString());
			}

			return row;
		}
	}

	private static class MyHolder
	{
		TextView hotel_name,mobile,address,priceRange,distance,tvRating;
		LinearLayout ll_call_now,ll_booking_now;
		ImageView imgHotel;
	}



}
