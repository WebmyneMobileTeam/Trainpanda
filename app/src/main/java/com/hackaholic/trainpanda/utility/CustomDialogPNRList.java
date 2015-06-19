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
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.fragments.PNRFragment;
import com.hackaholic.trainpanda.ui.fragments.TrainRoutesFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(act, "user_pref", 0);
        final SMS_DATA pnrobj = complexPreferences.getObject("sms", SMS_DATA.class);

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
            Log.e("Url : ", "" + url);
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


                    PNR cuurentPNR = new GsonBuilder().create().fromJson(jsonObject.toString(), PNR.class);


                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(act, "user_pref", 0);
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
                        //  saveDataToServer();

                        dismiss();


                        Fragment fragment = new TrainRoutesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("trainNo", trainNumber);
                        Log.e("pnr", cuurentPNR.pnr);
                        bundle.putString("pnr", cuurentPNR.pnr);
                        bundle.putString("doj", cuurentPNR.doj);

                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = act.getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();

                    }
                } catch (Exception e) {
                    Log.e("exc in pnr", e.toString());
                    e.printStackTrace();
                }
            } else {
                pringMessage("Response is null.");
            }


            dialog.dismiss();



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
