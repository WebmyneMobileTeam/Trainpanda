package com.hackaholic.trainpanda.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.activities.SlidingActivity;
import com.hackaholic.trainpanda.utility.ExpandablePanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import Model.PNR;

public class TrainRoutesFragment extends Fragment implements OnClickListener
{
	private ArrayList<String> al_booking_status;
	private ArrayList<String> al_current_status;
	private ArrayList<String> al_no;
	private String totalPassengers="";

	private TextView train_route_train_name;
	private TextView train_route_train_code;
	private ListView train_route_listview,pnr_listview;
	private AutoCompleteTextView train_route_actv_train_number;
	private ProgressBar progressBar_train_routes;
	private TextView train_route_tv_go;
	String tNO;
	ExpandablePanel panel;
	LinearLayout pnr_expand , ll_pnr_third ,ll_pnr_fourth;
	ArrayList<String> al_code;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args  != null && args.containsKey("trainNo")){
			tNO= args.getString("trainNo");

		}

	}

	public TrainRoutesFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.train_route, container, false);
		initializeViews(rootView);		
		return rootView;

	}

	private void initializeViews(View rootView)
	{
		initializeTextViews(rootView);
		initiaiizeListViews(rootView);
		progressBar_train_routes = (ProgressBar) rootView.findViewById(R.id.progressBar_train_routes);
		//AutoCompleteTextview for source stations
		train_route_actv_train_number=(AutoCompleteTextView)rootView.findViewById(R.id.train_route_actv_train_number);
		train_route_actv_train_number.setThreshold(1);
		train_route_actv_train_number.setText(tNO);
		pnr_expand = (LinearLayout)rootView.findViewById(R.id.ll_pnr_first);

		pnr_listview = (ListView)rootView.findViewById(R.id.pnr_listview);

		ll_pnr_third = (LinearLayout)rootView.findViewById(R.id.ll_pnr_third);
		//ll_pnr_fourth = (LinearLayout)rootView.findViewById(R.id.ll_pnr_fourth);


		train_route_tv_go.performClick();

		panel = (ExpandablePanel) rootView.findViewById(R.id.expandablePanel);
		// panel.setCollapsedHeight(10);
		train_route_actv_train_number.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				if(train_route_actv_train_number.getText().toString().length()>4)
				{
					train_route_tv_go.performClick();
					new TrainNumberTask().execute(train_route_actv_train_number.getText().toString().trim());
				}
			}
		});



		panel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {

			public void onCollapse(View handle, View content) {
				pnr_expand.setVisibility(View.GONE);
			//	ll_pnr_third.setVisibility(View.GONE);
			//	ll_pnr_fourth.setVisibility(View.GONE);
				TextView btn = (TextView) handle;
				btn.setText("Show");
				panel.setCollapsedHeight(100);
			}

			public void onExpand(View handle, View content) {
				TextView btn = (TextView) handle;
				pnr_expand.setVisibility(View.VISIBLE);


				ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
				PNR cuurentPNR = complexPreferences.getObject("current-pnr", PNR.class);
				pnr_listview.setVisibility(View.VISIBLE);
				Log.e("on expand panel","yes");
				pnr_listview.setAdapter(
						new MyAdapterPNR(cuurentPNR, getActivity()));


				//	ll_pnr_third.setVisibility(View.VISIBLE);
				//ll_pnr_fourth.setVisibility(View.VISIBLE);
				panel.setCollapsedHeight(10);
				btn.setText("Hide");
			}
		});
	}

	private class TrainNumberTask extends AsyncTask<String,Void,String>
	{
		@Override
		protected void onPreExecute()
		{
			progressBar_train_routes.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params)
		{
			return hitServer(params[0]);
		}

		private String hitServer(String value)
		{
			String response=null;
			String url="http://api.railwayapi.com/suggest_train/trains/"+value+"/apikey/"+getResources().getString(R.string.key1)+"/";
			//String url="http://api.railwayapi.com/suggest_station/name/"+value+"/apikey/"+getResources().getString(R.string.key1)+"/";
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
			progressBar_train_routes.setVisibility(View.GONE);

			if(result!=null)
			{
				//Toast.makeText(getActivity(), ""+result, Toast.LENGTH_LONG).show();
				Log.e("REsponse", "" + result);

				ArrayList<String> al_train_numbers=new ArrayList<String>();
				try
				{
					JSONObject jsonObject=new JSONObject(result);
					JSONArray jsonArray=jsonObject.getJSONArray("train");
					for(int i=0;i<jsonArray.length();i++)
					{
						String number=(String)jsonArray.get(i);
						Log.e("Train Numbers : ", "" + number);
						al_train_numbers.add(number);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				ArrayAdapter<String> adapter2=new ArrayAdapter<String>(getActivity(),
						R.layout.single_row_textview,R.id.signle_row_textview_tv,al_train_numbers);
				train_route_actv_train_number.setAdapter(adapter2);
			}
			else
			{
				Toast.makeText(getActivity(), "Response Is null", Toast.LENGTH_SHORT).show();
			}
		}
	}


	private void initiaiizeListViews(View rootView)
	{
		train_route_listview=(ListView)rootView.findViewById(R.id.train_route_listview);
		train_route_listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				String code = al_code.get(position);
				//Toast.makeText(getActivity(),""+code,Toast.LENGTH_SHORT).show();

				PrefUtils.setCurrentStationCode(getActivity(),code);

				Intent i = new Intent(getActivity(),SlidingActivity.class);
				//i.putExtra("stCode",code);
				startActivity(i);
				//startActivity(new Intent(getActivity(),SlidingActivity.class));
			}
		});


		//To set alarm
		train_route_listview.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				Toast.makeText(getActivity(), "Set Alarm ?", Toast.LENGTH_LONG).show();
				return true;
			}
		});
	}

	private void initializeTextViews(View rootView)
	{
		train_route_train_name=(TextView)rootView.findViewById(R.id.train_route_train_name);
		train_route_train_code=(TextView)rootView.findViewById(R.id.train_route_train_code);
		train_route_tv_go=(TextView)rootView.findViewById(R.id.train_route_tv_go);
		train_route_tv_go.setOnClickListener(this);
	}

	private class TrainRouteAsync extends AsyncTask<String, Void, String>
	{
		private ProgressDialog dialog;
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
			return hitServer(params[0]);
		}

		private String hitServer(String url)
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

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			//Initialize Arraylist
			ArrayList<String> al_lat=new ArrayList<String>();
			ArrayList<String> al_scharr=new ArrayList<String>();
			ArrayList<String> al_fullname=new ArrayList<String>();
			ArrayList<String> al_schdep=new ArrayList<String>();
			ArrayList<String> al_state=new ArrayList<String>();
			ArrayList<String> al_no=new ArrayList<String>();
			 al_code=new ArrayList<String>();
			ArrayList<String> al_lng=new ArrayList<String>();
			if(result!=null)
			{
				try
				{
					JSONObject jsonObject=new JSONObject(result);
					System.out.println("Response from server :: "+result);
					JSONObject trainJsonObject=jsonObject.getJSONObject("train");
					String train_number=trainJsonObject.getString("number");
					String train_name=trainJsonObject.getString("name");



					//Get Route Array
					JSONArray routeJsonArray=jsonObject.getJSONArray("route");
					for(int i=0;i<routeJsonArray.length();i++)
					{
						JSONObject jsonObject2=routeJsonArray.getJSONObject(i);
						al_lat.add(jsonObject2.getString("lat"));
						al_scharr.add(jsonObject2.getString("scharr"));
						al_fullname.add(jsonObject2.getString("fullname"));
						al_schdep.add(jsonObject2.getString("schdep"));
						al_state.add(jsonObject2.getString("state"));
						al_no.add(jsonObject2.getString("no"));
						al_code.add(jsonObject2.getString("code"));
						//System.out.println(al_code+"ALCODE");
						al_lng.add(jsonObject2.getString("lng"));
					}


					train_route_train_name.setText(" "+train_name+" ");
					train_route_train_code.setText(" "+train_number+" ");
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			train_route_listview.setAdapter(new MyAdapter(getActivity(),
					al_lat, al_scharr, al_fullname, al_schdep, al_state, al_no, al_code, al_lng));
		}
	}



	private class MyAdapterPNR extends BaseAdapter
	{
		private PNR valuesPNR;
		private int rows;
		private Context context;
		public MyAdapterPNR(PNR currentpnr,Context context)
		{

			this.valuesPNR = currentpnr;
			this.context=context;
			Log.e("pnr : ", "" + this.valuesPNR.pnr);
			Log.e("Cchart preaperd : ", "" + this.valuesPNR.chart_prepared);
			Log.e("train name : ", "" + this.valuesPNR.train_name);
			Log.e("Total Passengers : ", "" + this.valuesPNR.total_passengers);
		}
		@Override
		public int getCount()
		{
			return 3;
		}
		@Override
		public Object getItem(int position)
		{
			return position;
		}
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row=convertView;

				LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				row=inflater.inflate(R.layout.single_row_pnr,parent,false);


			TextView single_row_pnr_tv_passengers=(TextView)row.findViewById(R.id.single_row_pnr_tv_passengers);
			TextView single_row_pnr_tv_seats=(TextView)row.findViewById(R.id.single_row_pnr_tv_seats);
			TextView single_row_pnr_tv_status=(TextView)row.findViewById(R.id.single_row_pnr_tv_seats);

			single_row_pnr_tv_passengers.setText("Passenger ");
			/*for(int i=0;i<valuesPNR.passengers.size();i++){
				//holder.single_row_pnr_tv_passengers.setText("Passenger "+valuesPNR.passengers.get(i).no+"");
				holder.single_row_pnr_tv_seats.setText(valuesPNR.passengers.get(i).booking_status+"  ");
				holder.single_row_pnr_tv_status.setText(valuesPNR.passengers.get(i).current_status+"");

			}*/

			return row;
		}
	}













	private class MyAdapter extends BaseAdapter
	{
		private Context context;
		private ArrayList<String> al_lat;
		private ArrayList<String> al_scharr;
		private ArrayList<String> al_fullname;
		private ArrayList<String> al_schdep;
		private ArrayList<String> al_state;
		private ArrayList<String> al_no;
		private ArrayList<String> al_code;
		private ArrayList<String> al_lng;

		public MyAdapter(Context context,ArrayList<String> al_lat,ArrayList<String> al_scharr,
				ArrayList<String> al_fullname,ArrayList<String> al_schdep,ArrayList<String> al_state,
				ArrayList<String> al_no,ArrayList<String> al_code,ArrayList<String> al_lng)
		{
			this.context=context;
			this.al_lat=al_lat;
			this.al_scharr=al_scharr;
			this.al_fullname=al_fullname;
			this.al_schdep=al_schdep;
			this.al_state=al_state;
			this.al_no=al_no;
			this.al_code=al_code;
			this.al_lng=al_lng;
		}

		@Override
		public int getCount() 
		{
			return al_lat.size();
		}

		@Override
		public Object getItem(int position)
		{
			return al_lat.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row=convertView;
			MyHolder holder;
			if(row==null)
			{
				holder=new MyHolder();
				LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				row=inflater.inflate(R.layout.single_row_train_route, parent,false);
				holder.train_route_arival_time=(TextView) row.findViewById(R.id.train_route_arival_time);
				holder.train_route_departure_time=(TextView) row.findViewById(R.id.train_route_departure_time);
				holder.train_route_avg_delay_time=(TextView) row.findViewById(R.id.train_route_avg_delay_time);
				holder.train_route_distance_next_station=(TextView) row.findViewById(R.id.train_route_distance_next_station);
				holder.train_route_halt_time=(TextView) row.findViewById(R.id.train_route_halt_time);
				holder.train_route_train_name=(TextView) row.findViewById(R.id.train_route_train_name);
				row.setTag(holder);
				//Log.e("ALCODE ",""+al_code);
			}
			else
			{
				holder=(MyHolder) row.getTag();	
			}
			holder.train_route_arival_time.setText(" - "+al_scharr.get(position)+" ");
			holder.train_route_departure_time.setText(" - "+al_schdep.get(position)+" ");
			holder.train_route_train_name.setText(""+al_fullname.get(position)+" ");

			if(!al_scharr.get(position).trim().equals("Source") && !al_schdep.get(position).trim().equals("Destination") )
			{
				holder.train_route_halt_time.setText(calculateHaltTime(al_scharr.get(position).trim(),
						al_schdep.get(position).trim()));
			}
			else if(al_scharr.get(position).trim().equals("Source") )
			{
				holder.train_route_halt_time.setText("S");
			}
			else if(al_schdep.get(position).trim().equals("Destination") )
			{
				holder.train_route_halt_time.setText("D");
			}
			else 
			{
				holder.train_route_halt_time.setText(".......");	
			}

			return row;
		}
	}

	private static class MyHolder
	{
		TextView train_route_arival_time,train_route_departure_time,
		train_route_avg_delay_time,train_route_distance_next_station,train_route_halt_time
		,train_route_train_name;
	}

	private String calculateHaltTime(String start_time,String end_time)
	{
		String str="";
		try 
		{
			DateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date start = format.parse(start_time);
			Date end = format.parse(end_time);

			long difference = end.getTime() - start.getTime();

			long hours = TimeUnit.MILLISECONDS.toHours(difference);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;

			if(String.valueOf(hours).length()==1 && String.valueOf(minutes).length()==1)
			{
				str="0"+hours+":0"+minutes;
			}
			else if(String.valueOf(hours).length()==1 && String.valueOf(minutes).length()==2)
			{
				str="0"+hours+":"+minutes;
			}
			else if(String.valueOf(hours).length()==2 && String.valueOf(minutes).length()==1)
			{
				str=""+hours+":0"+minutes;
			}
			else if(String.valueOf(hours).length()==2 && String.valueOf(minutes).length()==2)
			{
				str=""+hours+":"+minutes;
			}
			else
			{
				str="";
			}
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.train_route_tv_go:
			if(!train_route_actv_train_number.getText().toString().trim().equals(""))
			{
				String url="http://api.railwayapi.com/route/train/"+train_route_actv_train_number.getText().toString().trim()+"/apikey/"+getResources().getString(R.string.key1)+"/";
				new TrainRouteAsync().execute(url);
			}
			else
			{
				Toast.makeText(getActivity(), "Please Enter Train Number...!", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

}
