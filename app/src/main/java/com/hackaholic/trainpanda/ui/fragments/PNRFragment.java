package com.hackaholic.trainpanda.ui.fragments;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
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
import com.hackaholic.trainpanda.ServiceHandler.PnrSmsReceiver;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.ui.activities.MainActivity;
import com.hackaholic.trainpanda.utility.CustomDialogPNRList;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import Model.PNR;
import Model.SMS_DATA;
import Model.SMS_PNR;

public class PNRFragment extends Fragment implements OnClickListener {
    private ImageView iv_b;
    private TextView pnr_tv_go, pnr_tv_from_name, pnr_tv_from_code, pnr_tv_from_date, pnr_tv_from_time, pnr_tv_train_name,
            pnr_tv_duration, pnr_tv_status, pnr_tv_to_name, pnr_tv_to_code, pnr_tv_to_date, pnr_tv_to_time;
    static EditText pnr_ed_pnr_no;
    private ListView pnr_listview;
    private TextView pnr_tv_train_number,buttonPNRSMS;
    private RelativeLayout rl_pnr_second;
    private LinearLayout pnr_third;
    private LinearLayout food_layout_send;
    private ImageView pnr_iv_hotel_booking;
    private ImageView pnr_iv_food;
    private ImageView pnr_iv_cab_booking;
    private ImageView pnr_iv_restaurant_booking;
    private ImageView pnr_iv_train_route;
    private TextView pnr_tv_add, pnr_tv_sdl_dep;
    private SharedPreferences sharedPreferences;

    private String fromStationCode = "", fromStationName = "", pnr = "", doj = "",
            toStationCode = "", toStationName = "", boardingPointStationCode = "", boardingPointStationName = "",
            trainNumber = "", reservationUptoStationCode = "", reservationUptoStationName = "", trainName = "";
    private String totalPassengers = "";
    private String classRailway;
    private ArrayList<String> al_booking_status;
    private ArrayList<String> al_current_status;
    private ArrayList<String> al_no;
    private ImageView pnr_iv_running_status;
    PNR cuurentPNR;

    public PNRFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.pnr, container, false);
        sharedPreferences = getActivity().getSharedPreferences("TrainPanda", getActivity().MODE_PRIVATE);
        /*iv_b=(ImageView)rootView.findViewById(R.id.iv_b);
		iv_b.setOnClickListener(this);*/
        initializeTextViews(rootView);
        initializeEditTexts(rootView);
        initializeImgaeViews(rootView);
        pnr_listview = (ListView) rootView.findViewById(R.id.pnr_listview);


        Log.e("Customer id : ", sharedPreferences.getString("customer_id", "").trim());


        buttonPNRSMS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSMS();
            }
        });

        return rootView;
    }

    private void fetchSMS(){
        boolean isSMSFind=false;
        ArrayList<SMS_PNR> smsPNR = new ArrayList<SMS_PNR>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c= getActivity().getContentResolver().query(uri, null, null, null, null);
       // startManagingCursor(c);
        // Read the sms data and store it in the list
        String sms = "";
        if(c.moveToFirst()) {
            for(int i=0; i < c.getCount(); i++) {
                sms = " " + c.getString(c.getColumnIndexOrThrow("body")).toString() ;
                if(sms.contains("PNR:")) {

                    Log.e("#### PNR SMS ",sms);

                    SMS_PNR smspnrdata = new SMS_PNR();
                    smspnrdata.PNR = sms;

                    smsPNR.add(smspnrdata);


                    isSMSFind = true;
                }

               // SMSData sms = new SMSData();
               // sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
               // sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
               // smsList.add(sms);
                c.moveToNext();
            }
        }
        c.close();


        if(isSMSFind) {
            SMS_DATA mainSMS = new SMS_DATA();
            mainSMS.SMSData = smsPNR;

            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
            complexPreferences.putObject("sms", mainSMS);
            complexPreferences.commit();
        }

        CustomDialogPNRList pnrBox = new CustomDialogPNRList(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        pnrBox.show();


    }

    private void initializeImgaeViews(View rootView) {
        rl_pnr_second = (RelativeLayout) rootView.findViewById(R.id.rl_pnr_second);
        pnr_third = (LinearLayout) rootView.findViewById(R.id.pnr_third);
        //food_layout_send = (LinearLayout)rootView.findViewById(R.id.food_layout_send);
        //pnr_iv_hotel_booking=(ImageView)rootView.findViewById(R.id.pnr_iv_hotel_booking);
        //pnr_iv_hotel_booking.setOnClickListener(this);
        //pnr_iv_cab_booking=(ImageView)rootView.findViewById(R.id.pnr_iv_cab_booking);
        //pnr_iv_cab_booking.setOnClickListener(this);
        //pnr_iv_restaurant_booking=(ImageView)rootView.findViewById(R.id.pnr_iv_restaurant_booking);
        //pnr_iv_restaurant_booking.setOnClickListener(this);
        //pnr_iv_train_route= (ImageView)rootView.findViewById(R.id.tain_route);
        //pnr_iv_train_route.setOnClickListener(this);
        //pnr_iv_food= (ImageView)rootView.findViewById(R.id.food_pnr);
        //pnr_iv_food.setOnClickListener(this);
        //pnr_tv_add=(TextView)rootView.findViewById(R.id.pnr_tv_add);
        //pnr_tv_add.setOnClickListener(this);
        //pnr_iv_running_status=(ImageView)rootView.findViewById(R.id.pnr_iv_running_status);
        //pnr_iv_running_status.setOnClickListener(this);
    }

    void initializeEditTexts(View rootView) {
        pnr_ed_pnr_no = (EditText) rootView.findViewById(R.id.ed_pnr_no_input);
    }

    public static void updateMessageBox(String msg) {
        pnr_ed_pnr_no.setText(msg);
    }

    private void initializeTextViews(View rootView) {
        buttonPNRSMS= (TextView) rootView.findViewById(R.id.buttonPNRSMS);
        pnr_tv_go = (TextView) rootView.findViewById(R.id.pnr_tv_go);
        pnr_tv_go.setOnClickListener(this);
        pnr_tv_from_name = (TextView) rootView.findViewById(R.id.pnr_tv_from_name);
        pnr_tv_sdl_dep = (TextView) rootView.findViewById(R.id.pnr_tv_sdl_dep);
        pnr_tv_from_code = (TextView) rootView.findViewById(R.id.pnr_tv_from_code);
        pnr_tv_from_date = (TextView) rootView.findViewById(R.id.pnr_tv_from_date);
        pnr_tv_from_time = (TextView) rootView.findViewById(R.id.pnr_tv_from_time);
        pnr_tv_train_name = (TextView) rootView.findViewById(R.id.pnr_tv_train_name);
        //pnr_tv_train_number=(TextView)rootView.findViewById(R.id.pnr_tv_train_number);
        //pnr_tv_train_number.setOnClickListener(this);
        //pnr_tv_duration=(TextView)rootView.findViewById(R.id.pnr_tv_duration);
        pnr_tv_status = (TextView) rootView.findViewById(R.id.pnr_tv_status);
        pnr_tv_to_name = (TextView) rootView.findViewById(R.id.pnr_tv_to_name);
        pnr_tv_to_code = (TextView) rootView.findViewById(R.id.pnr_tv_to_code);
        pnr_tv_to_date = (TextView) rootView.findViewById(R.id.pnr_tv_to_date);
        pnr_tv_to_time = (TextView) rootView.findViewById(R.id.pnr_tv_to_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
		/*case R.id.pnr_iv_running_status:
			if(!pnr_tv_train_number.getText().toString().trim().equals("Number")
					&& !pnr_tv_train_number.getText().toString().trim().equals(""))
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.frame_container, 
						new RunningStatusFragment(
								pnr_tv_train_number.getText().toString().trim(),
								pnr_tv_from_date.getText().toString().trim(),
								pnr_tv_from_name.getText().toString().trim(),
								pnr_tv_to_name.getText().toString().trim()
								)).commit();
			}
			else
			{
				pringMessage("Please Submit Valid PNR Number.");
			}
			break;		

		case R.id.food_pnr:
			if(!pnr_ed_pnr_no.getText().toString().trim().equals(""))
			{
				FragmentManager fragmentManager2 = getFragmentManager();
				fragmentManager2.beginTransaction()
				.replace(R.id.frame_container,new RestaurantFragment(toStationCode.toString())).commit();
			}
			else
			{
				pringMessage("Please fill PNR Number.");
			}
			
			break;

		case R.id.tain_route:

			if(!pnr_ed_pnr_no.getText().toString().trim().equals(""))
			{
				System.out.println("1");
				if(pnr_tv_train_number.getText().toString().equals(trainNumber+" ")){
					System.out.println("2");
				String tno = pnr_tv_train_number.getText().toString();
				System.out.println("3");
				
				
				System.out.println("4");
                
                Fragment fragment = new TrainRoutesFragment(); 
                Bundle bundle = new Bundle();
				bundle.putString("trainNo", tno);
				fragment.setArguments(bundle);
				FragmentManager fragmentManager22= getFragmentManager();
	     		fragmentManager22.beginTransaction().replace(R.id.frame_container,fragment).commit();	
			}
				else {
					pringMessage("Error in PNR");
				}
			}
			else
			{
				pringMessage("Please fill PNR Number.");
			}
			
						
			break;*/

		/*case R.id.pnr_tv_add:

			if(!pnr_ed_pnr_no.getText().toString().trim().equals(""))
			{
				new SavePnrNumbersAsync(getActivity()).execute();
			}
			else
			{
				pringMessage("Please fill PNR Number.");
			}
			break;*/

			/*	case R.id.iv_b:
			Intent intent=new Intent(getActivity(),DialogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			break;*/

            case R.id.pnr_tv_go:
                if (!pnr_ed_pnr_no.getText().toString().trim().equals("")) {
                    String url = "http://api.railwayapi.com/pnr_status/pnr/" + pnr_ed_pnr_no.getText().toString().trim() + "/apikey/" + getResources().getString(R.string.key1) + "/";
                    Log.e("Url : ", "" + url);
                    new PNRAsync().execute(url);

                    //pnr_tv_train_number.setText(pnr_ed_pnr_no.getText().toString().trim());
					/*rl_pnr_second.setVisibility(View.VISIBLE);
					pnr_third.setVisibility(View.VISIBLE);
					pnr_tv_to_time.setVisibility(View.VISIBLE);
					pnr_tv_to_date.setVisibility(View.VISIBLE);
					pnr_tv_from_time.setVisibility(View.VISIBLE);
					pnr_tv_sdl_dep.setVisibility(View.VISIBLE);*/
                    //food_layout_send.setVisibility(View.VISIBLE);

                } else {
                    pringMessage("Please fill PNR Number.");
                }
                break;

		/*case R.id.pnr_tv_train_number:
			
			*//*FragmentManager fragmentManager_tno = getFragmentManager();
			fragmentManager_tno.beginTransaction().replace(R.id.frame_container, new ArravaliExpressFragment(pnr_tv_train_number.getText().toString().trim())).commit();*//*
			if(!pnr_tv_train_number.getText().toString().trim().equals("Number"))
			{
				FragmentManager fragmentManager1 = getFragmentManager();
				fragmentManager1.beginTransaction()
				.replace(R.id.frame_container, new ArravaliExpressFragment(pnr_tv_train_number.getText().toString().trim())).commit();
			}
			else
			{
				pringMessage("Please Select Train Number Please...!");
			}
			break;

		case R.id.pnr_iv_cab_booking:
			FragmentManager fragmentManager0 = getFragmentManager();
			fragmentManager0.beginTransaction()
			.replace(R.id.frame_container,new CabBookingFragment(fromStationCode)).commit();
			break;

		case R.id.pnr_iv_hotel_booking:
			if(!pnr_ed_pnr_no.getText().toString().trim().equals(""))
			{
				FragmentManager fragmentManager1 = getFragmentManager();
				fragmentManager1.beginTransaction()
				.replace(R.id.frame_container,new HotelBookingFragment("AAR")).commit();
			}
			else
			{
				pringMessage("Please fill PNR Number.");
			}
			
			break;

		case R.id.pnr_iv_restaurant_booking:
			FragmentManager fragmentManager2 = getFragmentManager();
			fragmentManager2.beginTransaction()
			.replace(R.id.frame_container,new RestaurantFragment(fromStationCode)).commit();
			break;*/

            default:
                break;
        }
    }

    private String parseDate(String doj) {
        ArrayList<String> al = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(doj, "-");
        while (stringTokenizer.hasMoreTokens()) {
            al.add(stringTokenizer.nextToken());
        }
        return al.get(2) + "" + (al.get(1).length() == 1 ? "0" + al.get(1) : al.get(1)) + "" + (al.get(0).length() == 1 ? "0" + al.get(0) : al.get(0));
    }

    private void getTrainStartEndTime(String trainNumber, String date) {
        ArrayList<String> al = new ArrayList<String>();
        String url = "http://api.railwayapi.com/live/train/" + trainNumber + "/doj/" + date + "/apikey/" + getResources().getString(R.string.key1) + "/";
        Log.e("URL : ", "" + url);
    }

    private void saveDataToServer() {
        getTrainStartEndTime(trainNumber, parseDate(doj));
        try {
            JSONObject customerJsonObject = new JSONObject();
            customerJsonObject.put("id", sharedPreferences.getString("customer_id", "").trim());
            Log.e("Customer Json Object : ", String.valueOf(customerJsonObject));

            //Starting Station
            JSONObject startingStationJsonObject = new JSONObject();
            startingStationJsonObject.put("code", "" + fromStationCode.trim());
            startingStationJsonObject.put("name", "" + fromStationName.trim());
            Log.e("Starting Json Object : ", String.valueOf(startingStationJsonObject));

            //End Station
            JSONObject endJsonObject = new JSONObject();
            endJsonObject.put("code", "" + toStationCode.trim());
            endJsonObject.put("name", "" + toStationName.trim());
            Log.e("End Json Object : ", String.valueOf(endJsonObject));

            //Train Number
            Log.e("Train Number : ", "" + trainNumber);

            //Passengers Count
            Log.e("Total Passengers : ", "" + totalPassengers);


            //Parse Booking Status
            ArrayList<String> temp = new ArrayList<String>();
            StringTokenizer stringTokenizer = new StringTokenizer(al_booking_status.get(0).trim(), ",");
            while (stringTokenizer.hasMoreTokens()) {
                temp.add(stringTokenizer.nextToken());
            }

            Log.e("bookingClass : ", "" + classRailway);
            Log.e("currentBookingStatus : ", "" + al_current_status.get(0));
            Log.e("bookingStatus : ", "" + temp.get(0).trim());
            Log.e("PNR Number : ", "" + pnr_ed_pnr_no.getText().toString().trim());

            Log.e("bookingQuota : ", temp.get(1));



            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sNo", Long.parseLong(pnr_ed_pnr_no.getText().toString().trim()));
            jsonObject.put("pnr", pnr_ed_pnr_no.getText().toString().trim());
            jsonObject.put("customer", customerJsonObject);
            jsonObject.put("trainNumber", Integer.parseInt(trainNumber.trim()));
            jsonObject.put("startingStation", startingStationJsonObject);


//Date Conversion starts
            SimpleDateFormat orgFormat = new SimpleDateFormat("dd-M-yyyy");
            String dateInString = cuurentPNR.doj;

            final Date doj = orgFormat.parse(dateInString);
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM  yyyy hh:mm:ss  z");

            // Give it to me in GMT time.
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String DOJinString = sdf.format(doj);
            Log.e("Date", "" + DOJinString);
//Date Conversion ends

            jsonObject.put("startDate",DOJinString);

            jsonObject.put("endStation", endJsonObject);
            jsonObject.put("bookingStatus", temp.get(0).trim());
            jsonObject.put("passengersCount", Integer.parseInt(totalPassengers.trim()));
            jsonObject.put("bookingClass", classRailway);
            jsonObject.put("currentBookingStatus", al_current_status.get(0));
            jsonObject.put("bookingQuota", temp.get(1).trim());

            Log.e("FINAL JSON : ", "" + jsonObject);

            try {

                JSONPost json1 = new JSONPost();
                json1.POST(getActivity(), "http://admin.trainpanda.com/api/pnrs", jsonObject.toString(), "Saving PNR Details...");
                json1.setPostResponseListener(new POSTResponseListener() {
                    @Override
                    public String onPost(String msg) {

                        Log.e("add pnr to server", "onPost response: " + msg);

                        return null;
                    }

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onBackground() {

                    }
                });
            }catch (Exception e){
                Log.e("exc1----",e.toString());
            }





        } catch (Exception e) {
            Log.e("exc2----",e.toString());
            e.printStackTrace();
        }
}



    private class SavePnrNumbersAsync extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ProgressDialog dialog;

        public SavePnrNumbersAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait...!");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //
            if (Integer.parseInt(totalPassengers.trim()) > 1) {
                //Insert Many
                getTrainStartEndTime(trainNumber,parseDate(doj));

                hitServer();
            } else {
                hitServer();
            }
            return null;
        }

        private String parseDate(String doj) {
            ArrayList<String> al = new ArrayList<String>();
            StringTokenizer stringTokenizer = new StringTokenizer(doj, "-");
            while (stringTokenizer.hasMoreTokens()) {
                al.add(stringTokenizer.nextToken());
            }
            return al.get(2) + "" + (al.get(1).length() == 1 ? "0" + al.get(1) : al.get(1)) + "" + (al.get(0).length() == 1 ? "0" + al.get(0) : al.get(0));
        }

        private void getTrainStartEndTime(String trainNumber, String date) {
            ArrayList<String> al = new ArrayList<String>();
            String url = "http://api.railwayapi.com/live/train/" + trainNumber + "/doj/" + date + "/apikey/" + getResources().getString(R.string.key1) + "/";
            Log.e("URL : ", "" + url);
        }

        private void hitServer() {
            try {
                JSONObject customerJsonObject = new JSONObject();
                customerJsonObject.put("id", sharedPreferences.getString("customer_id", "").trim());
                Log.e("Customer Json Object : ", String.valueOf(customerJsonObject));

                //Starting Station
                JSONObject startingStationJsonObject = new JSONObject();
                startingStationJsonObject.put("code", "" + fromStationCode.trim());
                startingStationJsonObject.put("name", "" + fromStationName.trim());
                Log.e("Starting Json Object : ", String.valueOf(startingStationJsonObject));

                //End Station
                JSONObject endJsonObject = new JSONObject();
                endJsonObject.put("code", "" + toStationCode.trim());
                endJsonObject.put("name", "" + toStationName.trim());
                Log.e("End Json Object : ", String.valueOf(endJsonObject));

                //Train Number
                Log.e("Train Number : ", "" + trainNumber);

                //Passengers Count
                Log.e("Total Passengers : ", "" + totalPassengers);


                //Parse Booking Status
                ArrayList<String> temp = new ArrayList<String>();
                StringTokenizer stringTokenizer = new StringTokenizer(al_booking_status.get(0).trim(), ",");
                while (stringTokenizer.hasMoreTokens()) {
                    temp.add(stringTokenizer.nextToken());
                }

                Log.e("bookingClass : ", "" + classRailway);
                Log.e("currentBookingStatus : ", "" + al_current_status.get(0));
                Log.e("bookingStatus : ", "" + temp.get(0).trim());
                Log.e("PNR Number : ", "" + pnr_ed_pnr_no.getText().toString().trim());
                Log.e("bookingQuota : ", temp.get(2));


                //Finally Post Data
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://admin.trainpanda.com/api/pnrs");
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("sNo", pnr_ed_pnr_no.getText().toString().trim()));
                nameValuePair.add(new BasicNameValuePair("pnr", pnr_ed_pnr_no.getText().toString().trim()));
                nameValuePair.add(new BasicNameValuePair("customer", "" + customerJsonObject));
                nameValuePair.add(new BasicNameValuePair("trainNumber", "" + trainNumber.trim()));
                nameValuePair.add(new BasicNameValuePair("startingStation", "" + startingStationJsonObject));
                nameValuePair.add(new BasicNameValuePair("endStation", "" + endJsonObject));
                nameValuePair.add(new BasicNameValuePair("bookingStatus", temp.get(0).trim()));
                nameValuePair.add(new BasicNameValuePair("passengersCount", "" + totalPassengers.trim()));
                nameValuePair.add(new BasicNameValuePair("bookingClass", classRailway));
                nameValuePair.add(new BasicNameValuePair("currentBookingStatus", al_current_status.get(0)));
                nameValuePair.add(new BasicNameValuePair("bookingQuota", temp.get(2).trim()));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sNo", Long.parseLong(pnr_ed_pnr_no.getText().toString().trim()));
                jsonObject.put("pnr", pnr_ed_pnr_no.getText().toString().trim());
                jsonObject.put("customer", customerJsonObject);
                jsonObject.put("trainNumber", Integer.parseInt(trainNumber.trim()));
                jsonObject.put("startingStation", startingStationJsonObject);
                jsonObject.put("endStation", endJsonObject);
                jsonObject.put("bookingStatus", temp.get(0).trim());
                jsonObject.put("passengersCount", Integer.parseInt(totalPassengers.trim()));
                jsonObject.put("bookingClass", classRailway);
                jsonObject.put("currentBookingStatus", al_current_status.get(0));
                jsonObject.put("bookingQuota", temp.get(2).trim());

                Log.e("FINAL JSON : ", "" + jsonObject);

                try {

                    JSONPost json1 = new JSONPost();
                    json1.POST(getActivity(), "http://admin.trainpanda.com/api/pnrs", jsonObject.toString(), "Saving Recipe...");
                    json1.setPostResponseListener(new POSTResponseListener() {
                        @Override
                        public String onPost(String msg) {

                            Log.e("add recipe", "onPost response: " + msg);
                            Toast.makeText(getActivity(), "Recipe added Succesfully", Toast.LENGTH_SHORT).show();

                            return null;
                        }

                        @Override
                        public void onPreExecute() {

                        }

                        @Override
                        public void onBackground() {

                        }
                    });
                }catch (Exception e){
                    Log.e("exc----",e.toString());
                }













              /*  httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    String json = EntityUtils.toString(entity);
                    Log.e("*****Http Post Response : " + response.getStatusLine().getStatusCode(), json);
                    Log.e("Server Error : " + response.getStatusLine().getStatusCode(), "Server Error");
                } else {
                    Log.e("Server Error : " + response.getStatusLine().getStatusCode(), "Server Error");
                    HttpEntity entity = response.getEntity();
                    String json = EntityUtils.toString(entity);
                    Log.e("*****Http Post Response : " + response.getStatusLine().getStatusCode(), json);
                }
                */


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView title = (TextView) getActivity().findViewById(R.id.lk_profile_header_textview);
        title.setText("Train Panda");

        ImageView imgToolbarOption = (ImageView) getActivity().findViewById(R.id.imgToolbarOption);
        imgToolbarOption.setVisibility(View.GONE);

    }

    private void pringMessage(String msg) {
        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_LONG).show();
    }

    //Pnr api hitting async task
    private class PNRAsync extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait...!");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return hitUrl(params[0]);
        }

        private String hitUrl(String url) {
            String response = null;
            try {
                ServiceHandler handler = new ServiceHandler();
                response = handler.makeServiceCall(url, ServiceHandler.GET);

            } catch (Exception e) {
                Log.e("exc in pnr saveign",e.toString());
                e.printStackTrace();
            }
            return response;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    Log.e("pnr Response", jsonObject.toString());


                    cuurentPNR = new GsonBuilder().create().fromJson(jsonObject.toString(), PNR.class);


                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                    complexPreferences.putObject("current-pnr", cuurentPNR);
                    complexPreferences.commit();

                    System.out.println(result);
                    //Class
                    classRailway = jsonObject.getString("class");
                    //to_station
                    JSONObject toStationJsonObject = jsonObject.getJSONObject("to_station");
                    toStationCode = toStationJsonObject.getString("code");
                    toStationName = toStationJsonObject.getString("name");
                    //total_passengers
                    totalPassengers = jsonObject.getString("total_passengers");
                    //pnr
                    pnr = jsonObject.getString("pnr");
                    //doj
                    doj = jsonObject.getString("doj");
                    //boarding_point
                    JSONObject boardingPointJsonObject = jsonObject.getJSONObject("boarding_point");
                    boardingPointStationCode = boardingPointJsonObject.getString("code");
                    boardingPointStationName = boardingPointJsonObject.getString("name");
                    //train_num
                    trainNumber = jsonObject.getString("train_num");
                    //chart_prepared
                    String chartPrepared = jsonObject.getString("chart_prepared");
                    //from_station
                    JSONObject fromStation = jsonObject.getJSONObject("from_station");
                    fromStationCode = fromStation.getString("code");
                    fromStationName = fromStation.getString("name");
                    //reservation_upto
                    JSONObject reservationUptoJsonObject = jsonObject.getJSONObject("reservation_upto");
                    reservationUptoStationCode = reservationUptoJsonObject.getString("code");
                    reservationUptoStationName = reservationUptoJsonObject.getString("name");
                    //train_name
                    trainName = jsonObject.getString("train_name");
                    //passengers array
                    al_booking_status = new ArrayList<String>();
                    al_current_status = new ArrayList<String>();
                    al_no = new ArrayList<String>();

                    JSONArray passengersJsonArray = jsonObject.getJSONArray("passengers");
                    for (int i = 0; i < passengersJsonArray.length(); i++) {
                        JSONObject jsonObject2 = passengersJsonArray.getJSONObject(i);
                        al_booking_status.add(jsonObject2.getString("booking_status"));
                        al_current_status.add(jsonObject2.getString("current_status"));
                        al_no.add(jsonObject2.getString("no"));
                    }

                    if (cuurentPNR.pnr == null || cuurentPNR.pnr.equalsIgnoreCase("")) {
                        pringMessage("Could'nt Connect to Server. Please Try again");
                    } else {


                        // Saving data to server
                         saveDataToServer();


                        Fragment fragment = new TrainRoutesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("trainNo", trainNumber);
                        Log.e("pnr", cuurentPNR.pnr);
                        bundle.putString("pnr", cuurentPNR.pnr);
                        bundle.putString("doj", cuurentPNR.doj);

                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager22 = getFragmentManager();
                        fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();
                    }
                } catch (Exception e) {
                    Log.e("exc in pnr", e.toString());
                    e.printStackTrace();
                }
            } else {
                pringMessage("Response is null.");
            }
            //Set Values
            pnr_tv_from_name.setText(fromStationName + " ");
            pnr_tv_to_name.setText(toStationName + " ");
            pnr_tv_from_code.setText(fromStationCode + " ");
            System.out.println(fromStationCode);
            pnr_tv_to_code.setText(toStationCode + " ");
            pnr_tv_from_date.setText(doj + " ");
            pnr_tv_train_name.setText(trainName + " ");
            //pnr_tv_train_number.setText(trainNumber+" ");
            //pnr_tv_train_number.setText("Train No");

            dialog.dismiss();
            pnr_listview.setAdapter(
                    new MyAdapter(al_booking_status,
                            al_current_status,
                            al_no, (totalPassengers.trim().equals("") ? 0 : Integer.parseInt(totalPassengers.trim())), getActivity()));



        }
    }


    private class MyAdapter extends BaseAdapter {
        private ArrayList<String> al_booking_status;
        private ArrayList<String> al_current_status;
        private ArrayList<String> al_no;
        private int rows;
        private Context context;

        public MyAdapter(ArrayList<String> al_booking_status,
                         ArrayList<String> al_current_status, ArrayList<String> al_no, int rows, Context context) {
            this.al_booking_status = al_booking_status;
            this.al_current_status = al_current_status;
            this.al_no = al_no;
            this.rows = rows;
            this.context = context;
            Log.e("Booking Status : ", "" + this.al_booking_status);
            Log.e("Current Status : ", "" + this.al_current_status);
            Log.e("Number : ", "" + this.al_no);
            Log.e("Total Passengers : ", "" + this.rows);
        }

        @Override
        public int getCount() {
            return rows;
        }

        @Override
        public Object getItem(int position) {
            return al_booking_status.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            MyHolder holder;
            if (row == null) {
                holder = new MyHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_row_pnr, parent, false);
                holder.single_row_pnr_tv_passengers = (TextView) row.findViewById(R.id.single_row_pnr_tv_passengers);
                holder.single_row_pnr_tv_seats = (TextView) row.findViewById(R.id.single_row_pnr_tv_seats);
                holder.single_row_pnr_tv_status = (TextView) row.findViewById(R.id.single_row_pnr_tv_status);
                row.setTag(holder);
            } else {
                holder = (MyHolder) row.getTag();
            }
            holder.single_row_pnr_tv_passengers.setText("Passenger " + al_no.get(position) + "");
            holder.single_row_pnr_tv_seats.setText(al_booking_status.get(position) + "  ");
            holder.single_row_pnr_tv_status.setText(al_current_status.get(position) + "");
            return row;
        }
    }

    private static final class MyHolder {
        TextView single_row_pnr_tv_passengers, single_row_pnr_tv_seats, single_row_pnr_tv_status;
    }


}
