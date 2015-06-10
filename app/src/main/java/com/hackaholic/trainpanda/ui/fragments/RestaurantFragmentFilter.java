package com.hackaholic.trainpanda.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.activities.RestrauntDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.Restraunt;

public class RestaurantFragmentFilter extends Fragment implements OnClickListener
{
	Restraunt currRestr;
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


		restaurant_fragment_filter_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

				Intent in = new Intent(getActivity(), RestrauntDetail.class);
				in.putExtra("stCode",currRestr.Restraunt.get(i).stationCode);
				startActivity(in);
			/*
				Restaurant_details fragment = new Restaurant_details();
				Bundle bundle = new Bundle();
				bundle.putString("stCode", currRestr.Restraunt.get(i).stationCode);
				//Log.e("pnr",cuurentPNR.pnr);
				//bundle.putString("pnr", cuurentPNR.pnr);

				//fragment.setArguments(bundle);
				FragmentManager fragmentManager22 = getFragmentManager();
				fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).addToBackStack(null).commit();*/

			}
		});



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

			if(result!=null)
			{
				try
				{

					JSONObject mainObject = new JSONObject();
					JSONArray subArray = new JSONArray();

					JSONArray jsonArray=new JSONArray(result);

					if(jsonArray.length()>0)
					{
						for(int i=0;i<jsonArray.length();i++)
						{
							JSONObject jsonObject=jsonArray.getJSONObject(i);

							Log.e("resturnr list",jsonObject.toString());
							subArray.put(i,jsonObject);
						}

						mainObject.put("Restraunt", subArray);
						Log.e("final resturnr list", mainObject.toString());

						currRestr  = new GsonBuilder().create().fromJson(mainObject.toString(), Restraunt.class);


					}
					else
					{
						//printMessage("No Records Found...!");
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

			try {
				if (currRestr.Restraunt.size() == 0) {
					Toast.makeText(getActivity(), "No Restraunt Found !!!", Toast.LENGTH_SHORT).show();
				} else
					restaurant_fragment_filter_listview.setAdapter(new MyAdapter(getActivity(), currRestr));
			}catch (Exception e){
				Toast.makeText(getActivity(), "No Restraunt Found !!!", Toast.LENGTH_SHORT).show();
			}

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
		private Restraunt valuesRestraunt;

		public MyAdapter(Context context,Restraunt curr )
		{
			this.context=context;
			this.valuesRestraunt = curr;
		}
		@Override
		public int getCount()
		{
			return valuesRestraunt.Restraunt.size();
		}
		@Override
		public Object getItem(int position)
		{
			return valuesRestraunt.Restraunt.get(position);
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
				holder.tv_restaurant_name=(TextView)row.findViewById(R.id.nameHotel);
				holder.tv_restaurant_mobile=(TextView)row.findViewById(R.id.mobile);
				holder.menu=(TextView)row.findViewById(R.id.menu);
				holder.time=(TextView)row.findViewById(R.id.time);

				holder.minOrder=(TextView)row.findViewById(R.id.minOrder);
				holder.delivery=(TextView)row.findViewById(R.id.delivery);
				holder.veg=(LinearLayout)row.findViewById(R.id.ll_veg_boundry);
				holder.nonveg=(LinearLayout)row.findViewById(R.id.ll_nonveg_boundry);

				//holder.ll_give_ratings=(LinearLayout)row.findViewById(R.id.ll_give_ratings);
				row.setTag(holder);
			}
			else
			{
				holder=(MyHolder) row.getTag();
			}
			holder.tv_restaurant_name.setText(valuesRestraunt.Restraunt.get(position).name);
			holder.tv_restaurant_mobile.setText(""+valuesRestraunt.Restraunt.get(position).mobileNo);


			if(valuesRestraunt.Restraunt.get(position).jainFoodAvailable ){
				holder.menu.setText(" " +"JainFood");
			}


			if(valuesRestraunt.Restraunt.get(position).northIndia ){
			holder.menu.setText(" " +"North Indian");
			}


			if(valuesRestraunt.Restraunt.get(position).southIndia ){
			holder.menu.setText(" " +"South Indian");
			}

			if(valuesRestraunt.Restraunt.get(position).pizza ){
				holder.menu.setText(" " +"Pizza");
			}

			holder.time.setText(valuesRestraunt.Restraunt.get(position).morningOpeningTime+" - "+valuesRestraunt.Restraunt.get(position).morningClosingTime + " Hrs , "+valuesRestraunt.Restraunt.get(position).eveningOpeningTime+" - "+valuesRestraunt.Restraunt.get(position).eveningClosingTime+" Hrs");
			holder.minOrder.setText("Min. Order : "+valuesRestraunt.Restraunt.get(position).minOrderAmount+" INR");

			if(valuesRestraunt.Restraunt.get(position).deliveryCharges ==0)
				holder.delivery.setText("Delivery : Free");
			else
			holder.delivery.setText("Delivery : "+valuesRestraunt.Restraunt.get(position).deliveryCharges+" INR");


			if(valuesRestraunt.Restraunt.get(position).vegItems) {
				holder.veg.setVisibility(View.VISIBLE);
				holder.nonveg.setVisibility(View.GONE);
			}
			else{
				holder.veg.setVisibility(View.GONE);
				holder.nonveg.setVisibility(View.VISIBLE);
			}


			return row;
		}
	}

	private static class MyHolder
	{
		TextView tv_restaurant_name,menu,time,minOrder,delivery,tv_restaurant_timings,tv_restaurant_mobile;
		LinearLayout nonveg,veg;
	}



}
