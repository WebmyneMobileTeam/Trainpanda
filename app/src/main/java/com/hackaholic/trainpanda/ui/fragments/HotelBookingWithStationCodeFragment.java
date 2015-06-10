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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HotelBookingWithStationCodeFragment extends Fragment implements OnClickListener
{

	private TextView hotel_fragment_filter_tv_go;
	private ProgressBar hotel_fragment_filter_progressBar;
	private AutoCompleteTextView hotel_fragment_filter_actv_source_station;
	private ListView hotel_fragment_filter_listview;

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
			//Declares arraylist
			ArrayList<String> al_name=new ArrayList<String>();
			ArrayList<String> al_mobileNo=new ArrayList<String>();
			ArrayList<String> al_email=new ArrayList<String>();
			ArrayList<String> al_stationCode=new ArrayList<String>();
			ArrayList<String> al_text=new ArrayList<String>();
			ArrayList<String> al_distanceFromStation=new ArrayList<String>();
			ArrayList<String> al_avgCostPerDay=new ArrayList<String>();
			ArrayList<String> al_24HoursCheckIn=new ArrayList<String>();
			ArrayList<String> al_hotelCategory=new ArrayList<String>();
			ArrayList<String> al_addedOn=new ArrayList<String>();
			ArrayList<String> al_updatedOn=new ArrayList<String>();
			ArrayList<String> al_id=new ArrayList<String>();
			if(result!=null)
			{
				try
				{
					JSONArray jsonArray=new JSONArray(result);
					if(jsonArray.length()>0)
					{
						for(int i=0;i<jsonArray.length();i++)
						{
							JSONObject jsonObject=jsonArray.getJSONObject(i);
							al_name.add(jsonObject.getString("name"));
							al_mobileNo.add(jsonObject.getString("mobileNo"));
							al_email.add(jsonObject.getString("email"));
							al_stationCode.add(jsonObject.getString("stationCode"));
							al_text.add(jsonObject.getString("text"));
							al_distanceFromStation.add(jsonObject.getString("distanceFromStation"));
							al_avgCostPerDay.add(jsonObject.getString("avgCostPerDay"));
							al_24HoursCheckIn.add(jsonObject.getString("24HoursCheckIn"));
							al_hotelCategory.add(jsonObject.getString("hotelCategory"));
							al_addedOn.add(jsonObject.getString("addedOn"));
							al_updatedOn.add(jsonObject.getString("updatedOn"));
							al_id.add(jsonObject.getString("id"));
						}
					}
					else
					{
						printMessage("No Records Found...!");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				printMessage("Server Error,Response is null...!");	
			}

			dialog.dismiss();
			hotel_fragment_filter_listview.setAdapter(new MyAdapter(getActivity(),
					al_name, 
					al_mobileNo,
					al_email,
					al_stationCode,
					al_text,
					al_distanceFromStation,
					al_avgCostPerDay,
					al_24HoursCheckIn, 
					al_hotelCategory,
					al_addedOn, 
					al_updatedOn,
					al_id));
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
		private ArrayList<String> al_name;
		private ArrayList<String> al_mobileNo;
		private ArrayList<String> al_email;
		private ArrayList<String> al_stationCode;
		private ArrayList<String> al_text;
		private ArrayList<String> al_distanceFromStation;
		private ArrayList<String> al_avgCostPerDay;
		private ArrayList<String> al_24HoursCheckIn;
		private ArrayList<String> al_hotelCategory;
		private ArrayList<String> al_addedOn;
		private ArrayList<String> al_updatedOn;
		private ArrayList<String> al_id;

		public MyAdapter(Context context,
				ArrayList<String> al_name,
				ArrayList<String> al_mobileNo,
				ArrayList<String> al_email,
				ArrayList<String> al_stationCode,
				ArrayList<String> al_text,
				ArrayList<String> al_distanceFromStation,
				ArrayList<String> al_avgCostPerDay,
				ArrayList<String> al_24HoursCheckIn,
				ArrayList<String> al_hotelCategory,
				ArrayList<String> al_addedOn,
				ArrayList<String> al_updatedOn,
				ArrayList<String> al_id)
		{
			this.context=context;
			this.al_name=al_name;
			this.al_mobileNo=al_mobileNo;
			this.al_email=al_email;
			this.al_stationCode=al_stationCode;
			this.al_text=al_text;
			this.al_distanceFromStation=al_distanceFromStation;
			this.al_avgCostPerDay=al_avgCostPerDay;
			this.al_24HoursCheckIn=al_24HoursCheckIn;
			this.al_hotelCategory=al_hotelCategory;
			this.al_addedOn=al_addedOn;
			this.al_updatedOn=al_updatedOn;
			this.al_id=al_id;
		}
		@Override
		public int getCount() 
		{
			return al_name.size();
		}
		@Override
		public Object getItem(int position)
		{
			return al_name.get(position);
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
				row.setTag(holder);
			}
			else
			{
				holder=(MyHolder) row.getTag();
			}
			holder.hotel_name.setText(al_name.get(position));
			//holder.hotel_name.setText(al_mobileNo.get(position));


			return row;
		}
	}

	private static class MyHolder
	{
		TextView hotel_name,mobile;
		LinearLayout ll_call_now,ll_booking_now;
	}



}
