package com.hackaholic.trainpanda.utility;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.fragments.SlidingFragment;
import com.hackaholic.trainpanda.ui.fragments.TrainRoutesFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.PNR;
import Model.TRAIN_NAMES;

/**
 * Created by Android on 27-04-2015.
 */
public class CustomDialogBoxEditStation extends Dialog  implements
        View.OnClickListener{
    private ProgressDialog progressDialog;
    public FragmentActivity act;
    public Dialog d;
    public TextView txtUpdate,txtClose;

    AutoCompleteTextView edStat;
    private String UserId;
    ProgressBar pb_loading_indicator;

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
    ArrayList<String> all_train_station_fullname;
    ArrayList<String> all_train_station_code;

    long  POS_ST_CODE;
    String FULL_NAME;
    String CODE;
    boolean isStationSelected;

    public CustomDialogBoxEditStation(FragmentActivity context) {
        super(context);
        this.act = context;


    }

    public CustomDialogBoxEditStation(FragmentActivity context, int theme) {
        super(context, theme);
        this.act = context;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_station);

        pb_loading_indicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        edStat =  (AutoCompleteTextView)findViewById(R.id.edStat);



        edStat.setThreshold(2);
        edStat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (edStat.getText().toString().length() > 1) {
                    //edStat.performClick();
                    new TrainNumberTask().execute(edStat.getText().toString().trim());
                }

            }
        });

        edStat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //POS_ST_CODE = adapterView.getItemIdAtPosition(i);
                isStationSelected = true;
                FULL_NAME = all_train_station_fullname.get(i);
                CODE = all_train_station_code.get(i);
            }
        });




        txtUpdate = (TextView) findViewById(R.id.txtUpdate);
        txtClose = (TextView) findViewById(R.id.txtClose);

        txtUpdate.setOnClickListener(this);
        txtClose.setOnClickListener(this);
    }



    class TrainNumberTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pb_loading_indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return hitServer(params[0]);
        }

        private String hitServer(String value) {
            String response = null;
            String url = "http://api.railwayapi.com/suggest_station/name/" + value + "/apikey/" + act.getResources().getString(R.string.key1) + "/";
             try {
                ServiceHandler handler = new ServiceHandler();
                response = handler.makeServiceCall(url.replaceAll(" ", "%20"), ServiceHandler.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            pb_loading_indicator.setVisibility(View.GONE);

            if (result != null) {
                //Toast.makeText(getActivity(), ""+result, Toast.LENGTH_LONG).show();
                Log.e("REsponse", "" + result);

                all_train_station_code = new ArrayList<String>();
                all_train_station_fullname = new ArrayList<String>();
                try {

                    TRAIN_NAMES tnmames =  new GsonBuilder().create().fromJson(result.toString(), TRAIN_NAMES.class);
                    for(int i=0;i<tnmames.station.size();i++){

                        all_train_station_code.add(tnmames.station.get(i).code);
                        all_train_station_fullname.add(tnmames.station.get(i).fullname);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }




                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(act,
                        R.layout.single_row_textview, R.id.signle_row_textview_tv, all_train_station_code);


                edStat.setAdapter(adapter2);
                edStat.showDropDown();




            } else {
               // Toast.makeText(getActivity(), "Response Is null", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtUpdate:

                dismiss();

                if(isStationSelected) {

                    isStationSelected = false;

                    PrefUtils.setCurrentStationCode(act, CODE);

                    Bundle bun = new Bundle();
                    bun.putString("stName", FULL_NAME + " (" + CODE + ")");


                    SlidingFragment fragment = new SlidingFragment();
                    fragment.setArguments(bun);
                    FragmentManager fragmentManager22 = act.getSupportFragmentManager();
                    fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();
                }else{
                    pringMessage("Please Select Valid Station !!!");
                }



                break;

            case R.id.txtClose:
                    dismiss();
                break;

            default:
                break;
        }
       // dismiss();

    }

   private void processFetchResult(){

    }

    private void processUpdatePNR(){
/*
        if (!edPNR.getText().toString().trim().equals("")) {
            String url = "http://api.railwayapi.com/pnr_status/pnr/" + edPNR.getText().toString().trim() + "/apikey/" + act.getResources().getString(R.string.key1) + "/";
            Log.e("Url : ", "" + url);
            new PNRAsync().execute(url);



        } else {
            pringMessage("Please fill PNR Number.");
        }*/
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




    //end of main class
}
