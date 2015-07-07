package com.hackaholic.trainpanda.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
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
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.fragments.PNRFragment;
import com.hackaholic.trainpanda.ui.fragments.TrainRoutesFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;
import Model.PNR;
import Model.SMS_DATA;

/**
 * Created by Android on 27-04-2015.
 */
public class CustomDialogPNRList extends Dialog {
    private ProgressDialog progressDialog;

    public TextView txtUpdate,txtClose;

    EditText edPNR;
    private String UserId;


    private ImageView iv_b;
    private TextView pnr_tv_go, pnr_tv_from_name, pnr_tv_from_code, pnr_tv_from_date, pnr_tv_from_time, pnr_tv_train_name,
            pnr_tv_duration, pnr_tv_status, pnr_tv_to_name, pnr_tv_to_code, pnr_tv_to_date, pnr_tv_to_time;
    static EditText pnr_ed_pnr_no;
    private ListView pnr_listview;
    private TextView pnr_tv_train_number;
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
    SMS_DATA pnrobj;


    FragmentActivity act;
    public Dialog d;
    ListView pnrList;
    TextView txtNOSMS;
    public CustomDialogPNRList(Activity context) {
        super(context);



    }

    public CustomDialogPNRList(FragmentActivity context, int theme) {
        super(context, theme);
        this.act = context;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pnr_list);

//        ComplexPreferences complexPreferences2 = ComplexPreferences.getComplexPreferences(act, "sms_pref", 0);
//        pnrobj = complexPreferences2.getObject("sms2", SMS_DATA.class);

        pnrobj = PNRFragment.mainSMS;


        pnrList = (ListView)findViewById(R.id.pnrList);
        txtNOSMS = (TextView)findViewById(R.id.txtNOSMS);

        try{
            if(pnrobj!=null || pnrobj.SMSData.size()>0) {
                pnrList.setVisibility(View.VISIBLE);
                MyAdapter adp = new MyAdapter(pnrobj, act);
                pnrList.setAdapter(adp);
            }
        }catch (Exception e){
            pnrList.setVisibility(View.GONE);
            Log.e("----sms exc","no sms found");
        }



        pnrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                processUpdatePNR(pnrobj.SMSData.get(i).PNR);
                //PNRFragment.pnr_ed_pnr_no.setText(pnrobj.SMSData.get(i).PNR);
                dismiss();
            }
        });
    }

    private void processUpdatePNR(String smsPnr){


            String url = "http://api.railwayapi.com/pnr_status/pnr/" + smsPnr + "/apikey/" + act.getResources().getString(R.string.key1) + "/";
            Log.e("Url1 : ", "" + url);
            new PNRAsync().execute(url);

    }


    //Pnr api hitting async task
    private class PNRAsync extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(act);
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


                    cuurentPNR = new GsonBuilder().create().fromJson(result, PNR.class);


                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(act, "user_pref", 0);
                    complexPreferences.putObject("current-pnr", cuurentPNR);
                    complexPreferences.commit();

                    if (cuurentPNR.pnr == null || cuurentPNR.pnr.equalsIgnoreCase("")) {
                        pringMessage("Could'nt Connect to Server. Please Try again");
                    } else {


                        // Saving data to server
                        //saveDataToServer();

                        dismiss();


                        Fragment fragment = new TrainRoutesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("trainNo", ""+cuurentPNR.train_num);
                        Log.e("pnr", cuurentPNR.pnr);
                        bundle.putString("pnr", cuurentPNR.pnr);
                        bundle.putString("doj", cuurentPNR.doj);

                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = act.getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();

                    }



                } catch (Exception e) {
                    Log.e("exc in pnr111", e.toString());
                    e.printStackTrace();
                }
            } else {
                pringMessage("Response is null.");
            }


            dialog.dismiss();



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
        String url = "http://api.railwayapi.com/live/train/" + trainNumber + "/doj/" + date + "/apikey/" + act.getResources().getString(R.string.key1) + "/";
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
                json1.POST(act, "http://admin.trainpanda.com/api/pnrs", jsonObject.toString(), "Saving PNR Details...");
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




    void pringMessage(String msg){
        Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
    }


    class MyAdapter extends BaseAdapter {
        private Context context;
        SMS_DATA valuesPNR;

        MyAdapter(SMS_DATA obj,Context ctx){
            this.context = ctx;
            this.valuesPNR =  obj;
        }

        @Override
        public int getCount() {
            return valuesPNR.SMSData.size();
        }

        @Override
        public Object getItem(int i) {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.pnr_list_item, parent,false);;

            TextView pnrNo = (TextView)row.findViewById(R.id.pnrNumber);
            pnrNo.setTypeface(PrefUtils.getTypeFace(act));
            pnrNo.setText(valuesPNR.SMSData.get(position).PNR);


            return row;
        }
    }


    //end of main class
}
