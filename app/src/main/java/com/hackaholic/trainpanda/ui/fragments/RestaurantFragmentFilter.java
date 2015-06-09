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

public class RestaurantFragmentFilter extends Fragment implements OnClickListener
{
	private ListView restaurant_fragment_filter_listview;
	private TextView restaurant_fragment_filter_tv_go;
	private ProgressBar restaurant_fragment_filter_progressBar;
	private AutoCompleteTextView restaurant_fragment_filter_actv_source_station;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		View row=inflater.inflate(R.layout.restaurant_fragment_new, container,false);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);


		initializeViews(row);
		performTask();

		getRestaurantDetails();

		return row;
	}


	private void getRestaurantDetails(){

		String stCode = PrefUtils.getCurrentStationCode(getActivity());
		Log.e("stCode",stCode);

		new RestaurantApiAsync().execute(
				new String[]
						{

								stCode
						}
		);
	}

	private void performTask()
	{
		//ACTV for source Station
		restaurant_fragment_filter_actv_source_station.setThreshold(2);
		restaurant_fragment_filter_actv_source_station.addTextChangedListener(new TextWatcher()
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
				if(restaurant_fragment_filter_actv_source_station.getText().toString().length()>2)
				{
					new SourceStationAsyncTask().execute(restaurant_fragment_filter_actv_source_station.getText().toString().trim());
				}
			}
		});
	}

	private void initializeViews(View row)
	{
		initializeListView(row);
		initializeTextViews(row);
		initializeProgressBar(row);
		initializeACTV(row);
	}

	private void initializeACTV(View row)
	{
		restaurant_fragment_filter_actv_source_station=(AutoCompleteTextView)row.findViewById(R.id.restaurant_fragment_filter_actv_source_station);
	}

	private void initializeProgressBar(View row)
	{
		restaurant_fragment_filter_progressBar=(ProgressBar)row.findViewById(R.id.restaurant_fragment_filter_progressBar);
	}

	private void initializeTextViews(View row)
	{
		restaurant_fragment_filter_tv_go=(TextView)row.findViewById(R.id.restaurant_fragment_filter_tv_go);
		restaurant_fragment_filter_tv_go.setOnClickListener(this);
	}

	private void initializeListView(View row)
	{
		restaurant_fragment_filter_listview=(ListView)row.findViewById(R.id.restaurant_fragment_filter_listview);
	}
	//Async Task For Source station
	private class SourceStationAsyncTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			restaurant_fragment_filter_progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params)
		{
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			restaurant_fragment_filter_progressBar.setVisibility(View.GONE);
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
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),
						R.layout.single_row_textview,R.id.signle_row_textview_tv,al_fullname);
				restaurant_fragment_filter_actv_source_station.setAdapter(adapter);
			}
			else
			{
				Toast.makeText(getActivity(), "Response Is null", Toast.LENGTH_SHORT).show();
			}

		}
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.restaurant_fragment_filter_tv_go:
				if(!restaurant_fragment_filter_actv_source_station.getText().toString().trim().equals(""))
				{
					new RestaurantApiAsync().execute(
							new String[]
									{
											restaurant_fragment_filter_actv_source_station.getText().toString().trim(),
									}
					);
				}
				else
				{
					Toast.makeText(getActivity(), "Please fill station name...!", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
		}
	}


	private class RestaurantApiAsync extends AsyncTask<String,Void,String>
	{
		private ProgressDialog dialog;
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		/*	dialog=new ProgressDialog(getActivity());
			dialog.setCancelable(false);
			dialog.setMessage("Please Wait...!");
			dialog.show();*/
		}

		@Override
		protected String doInBackground(String... params)
		{
			//String station_code=getStationCode(getStationResponse(params[0]));

			String station_code = PrefUtils.getCurrentStationCode(getActivity());
			Log.e("Station Code : ", "" + station_code);
			String url= API.BASE_URL+"restaurants?filter[where][stationCode]="+station_code.trim();

			//String url="http://52.11.174.94/api/restaurants?filter[where][stationCode]="+params[0].trim();
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			//Declares arraylist
			ArrayList<String> al_name=new ArrayList<String>();
			ArrayList<String> al_code=new ArrayList<String>();
			ArrayList<String> al_mobileNo=new ArrayList<String>();
			ArrayList<String> al_email=new ArrayList<String>();
			ArrayList<String> al_stationCode=new ArrayList<String>();
			//ArrayList<String> al_text=new ArrayList<String>();
			ArrayList<String> al_distanceFromStation=new ArrayList<String>();
			ArrayList<String> al_avgPricePerPerson=new ArrayList<String>();
			ArrayList<String> al_minOrderAmount=new ArrayList<String>();
			ArrayList<String> al_deliveryCharges=new ArrayList<String>();
			ArrayList<String> al_railwayVendor=new ArrayList<String>();
			//ArrayList<String> al_jainFoodAvailable=new ArrayList<String>();
			//ArrayList<String> al_vegItems=new ArrayList<String>();
			//ArrayList<String> al_pizza=new ArrayList<String>();
			//ArrayList<String> al_northIndia=new ArrayList<String>();
			//ArrayList<String> al_southIndia=new ArrayList<String>();
			//ArrayList<String> al_thai=new ArrayList<String>();
			//ArrayList<String> al_rating=new ArrayList<String>();
			ArrayList<String> al_morningOpeningTime=new ArrayList<String>();
			ArrayList<String> al_morningClosingTime=new ArrayList<String>();
			ArrayList<String> al_eveningOpeningTime=new ArrayList<String>();
			ArrayList<String> al_eveningClosingTime=new ArrayList<String>();
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

							Log.e("resturnr list",jsonObject.toString());


							if(jsonObject.has("name")){
								al_name.add(jsonObject.getString("name"));
							}else al_name.add("no name");

							if(jsonObject.has("code")){
								al_name.add(jsonObject.getString("code"));
							}else al_name.add("no code");

							if(jsonObject.has("mobileNo")){
								al_name.add(jsonObject.getString("mobileNo"));
							}else al_name.add("no mobileNo");
							
							/*if(jsonObject.has("email")){
								al_name.add(jsonObject.getString("email"));
							}else al_name.add("no email");*/

							if(jsonObject.has("stationCode")){
								al_name.add(jsonObject.getString("stationCode"));
							}else al_name.add("no stationCode");
							
							/*if(jsonObject.has("text")){
								al_name.add(jsonObject.getString("text"));
							}else al_name.add("no text");
							
							if(jsonObject.has("distanceFromStation")){
								al_name.add(jsonObject.getString("distanceFromStation"));
							}else al_name.add("no distanceFromStation");
							
							if(jsonObject.has("avgPricePerPerson")){
								al_name.add(jsonObject.getString("avgPricePerPerson"));
							}else al_name.add("no avgPricePerPerson");*/

							if(jsonObject.has("minOrderAmount")){
								al_name.add(jsonObject.getString("minOrderAmount"));
							}else al_name.add("no minOrderAmount");

							if(jsonObject.has("deliveryCharges")){
								al_name.add(jsonObject.getString("deliveryCharges"));
							}else al_name.add("no deliveryCharges");

							if(jsonObject.has("railwayVendor")){
								al_name.add(jsonObject.getString("railwayVendor"));
							}else al_name.add("no railwayVendor");
							
							/*if(jsonObject.has("jainFoodAvailable")){
								al_name.add(jsonObject.getString("jainFoodAvailable"));
							}else al_name.add("no jainFoodAvailable");
							
							if(jsonObject.has("vegItems")){
								al_name.add(jsonObject.getString("vegItems"));
							}else al_name.add("no vegItems");
							
							if(jsonObject.has("pizza")){
								al_name.add(jsonObject.getString("pizza"));
							}else al_name.add("no pizza");
							
							if(jsonObject.has("northIndia")){
								al_name.add(jsonObject.getString("northIndia"));
							}else al_name.add("no northIndia");
							
							if(jsonObject.has("southIndia")){
								al_name.add(jsonObject.getString("southIndia"));
							}else al_name.add("no southIndia");
							
							if(jsonObject.has("thai")){
								al_name.add(jsonObject.getString("thai"));
							}else al_name.add("no thai");
							
							if(jsonObject.has("rating")){
								al_name.add(jsonObject.getString("rating"));
							}else al_name.add("no rating");*/

							if(jsonObject.has("morningOpeningTime")){
								al_name.add(jsonObject.getString("morningOpeningTime"));
							}else al_name.add("no morningOpeningTime");

							if(jsonObject.has("morningClosingTime")){
								al_name.add(jsonObject.getString("morningClosingTime"));
							}else al_name.add("no morningClosingTime");

							if(jsonObject.has("eveningOpeningTime")){
								al_name.add(jsonObject.getString("eveningOpeningTime"));
							}else al_name.add("no eveningOpeningTime");

							if(jsonObject.has("eveningClosingTime")){
								al_name.add(jsonObject.getString("eveningClosingTime"));
							}else al_name.add("no eveningClosingTime");

							if(jsonObject.has("addedOn")){
								al_name.add(jsonObject.getString("addedOn"));
							}else al_name.add("no addedOn");

							if(jsonObject.has("updatedOn")){
								al_name.add(jsonObject.getString("updatedOn"));
							}else al_name.add("no updatedOn");

							if(jsonObject.has("id")){
								al_name.add(jsonObject.getString("id"));
							}else al_name.add("no id");



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
			//dialog.dismiss();
			restaurant_fragment_filter_listview.setAdapter(new MyAdapter(getActivity(),
					al_name,
					al_code,
					al_mobileNo,
					al_email,
					al_stationCode,
					//al_text,
					al_distanceFromStation,
					al_avgPricePerPerson,
					al_minOrderAmount,
					al_deliveryCharges,
					al_railwayVendor,
					//al_jainFoodAvailable,
					//al_vegItems,
					//al_pizza,
					//al_northIndia, 
					//al_southIndia, 
					//al_thai, 
					//al_rating,
					al_morningOpeningTime,
					al_morningClosingTime,
					al_eveningOpeningTime,
					al_eveningClosingTime,
					al_addedOn,
					al_updatedOn,
					al_id));

		}
	}

	private void printMessage(String msg)
	{
		Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
	}


	private class MyAdapter extends BaseAdapter
	{
		private Context context;
		//Declares arraylist
		private ArrayList<String> al_name;
		private ArrayList<String> al_code;
		private ArrayList<String> al_mobileNo;
		private ArrayList<String> al_email;
		private ArrayList<String> al_stationCode;
		//private ArrayList<String> al_text;
		private ArrayList<String> al_distanceFromStation;
		private ArrayList<String> al_avgPricePerPerson;
		private ArrayList<String> al_minOrderAmount;
		private ArrayList<String> al_deliveryCharges;
		private ArrayList<String> al_railwayVendor;
		//private ArrayList<String> al_jainFoodAvailable;
		//private ArrayList<String> al_vegItems;
		//private ArrayList<String> al_pizza;
		//private ArrayList<String> al_northIndia;
		//private ArrayList<String> al_southIndia;
		//private ArrayList<String> al_thai;
		//private ArrayList<String> al_rating;
		private ArrayList<String> al_morningOpeningTime;
		private ArrayList<String> al_morningClosingTime;
		private ArrayList<String> al_eveningOpeningTime;
		private ArrayList<String> al_eveningClosingTime;
		private ArrayList<String> al_addedOn;
		private ArrayList<String> al_updatedOn;
		private ArrayList<String> al_id;

		public MyAdapter(Context context,
						 ArrayList<String> al_name,
						 ArrayList<String> al_code,
						 ArrayList<String> al_mobileNo,
						 ArrayList<String> al_email,
						 ArrayList<String> al_stationCode,
						 //ArrayList<String> al_text,
						 ArrayList<String> al_distanceFromStation,
						 ArrayList<String> al_avgPricePerPerson,
						 ArrayList<String> al_minOrderAmount,
						 ArrayList<String> al_deliveryCharges,
						 ArrayList<String> al_railwayVendor,
						 //ArrayList<String> al_jainFoodAvailable,
						 //ArrayList<String> al_vegItems,
						 //ArrayList<String> al_pizza,
						 //ArrayList<String> al_northIndia,
						 //ArrayList<String> al_southIndia,
						 //ArrayList<String> al_thai,
						 //ArrayList<String> al_rating,
						 ArrayList<String> al_morningOpeningTime,
						 ArrayList<String> al_morningClosingTime,
						 ArrayList<String> al_eveningOpeningTime,
						 ArrayList<String> al_eveningClosingTime,
						 ArrayList<String> al_addedOn,
						 ArrayList<String> al_updatedOn,
						 ArrayList<String> al_id)
		{
			this.context=context;
			this.al_name=al_name;
			this.al_code=al_code;
			this.al_mobileNo=al_mobileNo;
			this.al_email=al_email;
			this.al_stationCode=al_stationCode;
			//this.al_text=al_text;
			this.al_distanceFromStation=al_distanceFromStation;
			this.al_avgPricePerPerson=al_avgPricePerPerson;
			this.al_minOrderAmount=al_minOrderAmount;
			this.al_deliveryCharges=al_deliveryCharges;
			this.al_railwayVendor=al_railwayVendor;
			//this.al_jainFoodAvailable=al_jainFoodAvailable;
			//this.al_vegItems=al_vegItems;
			//this.al_pizza=al_pizza;
			//this.al_northIndia=al_northIndia;
			//this.al_southIndia=al_southIndia;
			//this.al_thai=al_thai;
			//this.al_rating=al_rating;
			this.al_morningOpeningTime=al_morningOpeningTime;
			this.al_morningClosingTime=al_morningClosingTime;
			this.al_eveningOpeningTime=al_eveningOpeningTime;
			this.al_eveningClosingTime=al_eveningClosingTime;
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
				row=inflater.inflate(R.layout.single_row_restaurant, parent,false);
				holder.tv_restaurant_name=(TextView)row.findViewById(R.id.tv_restaurant_name);
				holder.tv_restaurant_mobile=(TextView)row.findViewById(R.id.tv_restaurant_mobile);
				//holder.tv_n_india=(TextView)row.findViewById(R.id.tv_n_india);
				//holder.tv_n_pizza=(TextView)row.findViewById(R.id.tv_n_pizza);
				//holder.tv_s_indian=(TextView)row.findViewById(R.id.tv_s_indian);
				//holder.tv_veg=(TextView)row.findViewById(R.id.tv_veg);
				holder.tv_restaurant_timings=(TextView)row.findViewById(R.id.tv_restaurant_timings);
				holder.ll_order_now=(LinearLayout)row.findViewById(R.id.ll_order_now);
				holder.ll_call=(LinearLayout)row.findViewById(R.id.ll_call);
				//holder.ll_veg_boundry=(LinearLayout)row.findViewById(R.id.ll_veg_boundry);
				holder.ll_give_ratings=(LinearLayout)row.findViewById(R.id.ll_give_ratings);
				row.setTag(holder);
			}
			else
			{
				holder=(MyHolder) row.getTag();
			}
			holder.tv_restaurant_name.setText(al_name.get(position));
			//holder.tv_restaurant_mobile.setText(al_mobileNo.get(position));

			/*if(al_vegItems.get(position).trim().equals("true"))
			{
				holder.ll_veg_boundry.setBackgroundResource(R.drawable.green_boundry_hotel_booking);
				holder.tv_veg.setTextColor(Color.parseColor("#00AF00"));				
			}
			else
			{
				holder.ll_veg_boundry.setBackgroundResource(R.drawable.red_boundry_hotel_booking);
				holder.tv_veg.setTextColor(Color.parseColor("#F40101"));
			}*/
			
			/*if(al_northIndia.get(position).trim().equals("true"))
			{
				holder.tv_n_india.setText("yes");
			}
			else
			{
				holder.tv_n_india.setText("no");
			}
			
			if(al_southIndia.get(position).trim().equals("true"))
			{
				holder.tv_s_indian.setText("yes");
			}
			else
			{
				holder.tv_s_indian.setText("no");
			}
			
			if(al_pizza.get(position).trim().equals("true"))
			{
				holder.tv_n_pizza.setText("yes");
			}
			else
			{
				holder.tv_n_pizza.setText("no");
			}*/


			holder.ll_call.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					printMessage("Call "+position);
				}
			});

			holder.ll_order_now.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					printMessage("Order "+position);
				}
			});

			holder.ll_give_ratings.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					printMessage("Ratings"+position);
				}
			});

//			holder.tv_restaurant_timings.setText(" "+al_morningOpeningTime.get(position)+" to "+al_morningClosingTime.get(position)+" & "+al_eveningOpeningTime.get(position)+" to "+al_eveningClosingTime.get(position)+" ");
			return row;
		}
	}

	private static class MyHolder
	{
		TextView tv_restaurant_name,tv_n_india,tv_n_pizza,tv_s_indian,tv_veg,tv_restaurant_timings,tv_restaurant_mobile;
		LinearLayout ll_order_now,ll_call,ll_veg_boundry,ll_give_ratings;
	}



}
