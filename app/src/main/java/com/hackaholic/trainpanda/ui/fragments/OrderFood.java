package com.hackaholic.trainpanda.ui.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.helpers.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import Model.PNR;
import Model.TRAIN_NAMES;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFood extends Fragment {

    EditText etPNR;
    Button buttonGO,buttonPNRSMS;
    private ProgressBar progressBar_train_routes;

    private ImageView iv_b;
    private TextView pnr_tv_go, pnr_tv_from_name, pnr_tv_from_code, pnr_tv_from_date, pnr_tv_from_time, pnr_tv_train_name,
            pnr_tv_duration, pnr_tv_status, pnr_tv_to_name, pnr_tv_to_code, pnr_tv_to_date, pnr_tv_to_time;
    static EditText pnr_ed_pnr_no;
    AutoCompleteTextView etStnCode;
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


    String FULL_NAME;
    String CODE;
    boolean isStationSelected;
    public OrderFood() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageView imgToolbarOption = (ImageView) getActivity().findViewById(R.id.imgToolbarOption);
        imgToolbarOption.setVisibility(View.GONE);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_food, container, false);
        etStnCode= (AutoCompleteTextView)view.findViewById(R.id.etStnCode);
        etStnCode.setThreshold(2);

        progressBar_train_routes = (ProgressBar) view.findViewById(R.id.progressBar_train_routes);
        etPNR = (EditText)view.findViewById(R.id.etPNR);
        buttonGO = (Button)view.findViewById(R.id.buttonGO);


        TextView txtOR = (TextView)view.findViewById(R.id.txtOR);
        txtOR.setTypeface(PrefUtils.getTypeFace(getActivity()));
        TextView tv_detail = (TextView)view.findViewById(R.id.tv_detail);
        tv_detail.setTypeface(PrefUtils.getTypeFace(getActivity()));
        etPNR.setTypeface(PrefUtils.getTypeFace(getActivity()));
        etStnCode.setTypeface(PrefUtils.getTypeFace(getActivity()));
        buttonGO.setTypeface(PrefUtils.getTypeFace(getActivity()));

        etStnCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etStnCode.getText().toString().length() > 1) {
                    etStnCode.performClick();
                    new TrainNumberTask().execute(etStnCode.getText().toString().trim());
                }
            }
        });

        etStnCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                isStationSelected = true;
                FULL_NAME = all_train_station_fullname.get(i);
                CODE = all_train_station_code.get(i);
            }
        });


        buttonGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etPNR.getText().toString().length()!=0){
                    processUpdatePNR();
                }else{

                    if(isStationSelected) {
                        isStationSelected = false;

                        PrefUtils.setCurrentStationCode(getActivity(), CODE);

                        Bundle bun = new Bundle();
                        bun.putString("stName", FULL_NAME + " (" + CODE + ")");


                        SlidingFragment fragment = new SlidingFragment();
                        fragment.setArguments(bun);
                        FragmentManager fragmentManager22 = getFragmentManager();
                        fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();
                    }else {
                        pringMessage("Please Select Valid Station !!!");
                    }


                }



            }
        });

        return view;
    }


    private class TrainNumberTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
           progressBar_train_routes.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return hitServer(params[0]);
        }

        private String hitServer(String value) {
            String response = null;
            String url = "http://api.railwayapi.com/suggest_station/name/" + value + "/apikey/" + getResources().getString(R.string.key1) + "/";
            //String url="http://api.railwayapi.com/suggest_station/name/"+value+"/apikey/"+getResources().getString(R.string.key1)+"/";
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
            progressBar_train_routes.setVisibility(View.GONE);

            if (result != null) {
                //Toast.makeText(getActivity(), ""+result, Toast.LENGTH_LONG).show();
                Log.e("REsponse", "" + result);

                all_train_station_fullname = new ArrayList<String>();
                all_train_station_code = new ArrayList<String>();

                ArrayList<String> station_fullNAme_shortCode = new ArrayList<String>();

                try {

                    TRAIN_NAMES tnmames =  new GsonBuilder().create().fromJson(result.toString(), TRAIN_NAMES.class);
                    for(int i=0;i<tnmames.station.size();i++){
                        all_train_station_code.add(tnmames.station.get(i).code);
                        all_train_station_fullname.add(tnmames.station.get(i).fullname);
                        station_fullNAme_shortCode.add(tnmames.station.get(i).fullname+" ("+tnmames.station.get(i).code+")");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                        R.layout.suggestion_station, R.id.txtTitle, station_fullNAme_shortCode);

                etStnCode.setAdapter(adapter2);
                etStnCode.showDropDown();



            } else {
                Toast.makeText(getActivity(), "Response Is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processUpdatePNR(){

        if (!etPNR.getText().toString().trim().equals("")) {
            String url = "http://api.railwayapi.com/pnr_status/pnr/" + etPNR.getText().toString().trim() + "/apikey/" + getActivity().getResources().getString(R.string.key1) + "/";
            Log.e("Url : ", "" + url);
            new PNRAsync().execute(url);



        } else {
            pringMessage("Please fill PNR Number.");
        }
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


                    PNR cuurentPNR = new GsonBuilder().create().fromJson(jsonObject.toString(), PNR.class);


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

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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
            Log.e("bookingQuota : ", temp.get(2));


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
                Log.e("exc----",e.toString());
            }





        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void pringMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}
