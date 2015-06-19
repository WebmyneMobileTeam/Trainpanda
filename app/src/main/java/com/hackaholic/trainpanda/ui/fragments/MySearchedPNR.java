package com.hackaholic.trainpanda.ui.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.utility.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;
import Model.PNR;
import Model.TRAIN_NAMES;
import Model.TopCategories;
import Model.TopMenuItems;
import Model.TopSubCategories;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySearchedPNR extends Fragment {
    ProgressDialog pb;
    TextView txtPNRNUmber,pnrDate,pnrStatus;
    FETCH_PNR objPnr;

    ListView pnrList;
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
    public MySearchedPNR() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_searched_pnr, container, false);

         pnrList = (ListView)view.findViewById(R.id.pnrList);

        sharedPreferences = getActivity().getSharedPreferences("TrainPanda", getActivity().MODE_PRIVATE);

        Log.e("Customer id : ", sharedPreferences.getString("customer_id", "").trim());



        fetchPNRRecords();


        return view;
    }

    private void fetchPNRRecords(){

        pb =new ProgressDialog(getActivity());
        pb.setMessage("Loading details...");
        pb.show();

        Log.e("fetch pnr link ", "http://admin.trainpanda.com:80/api/pnrs/getByCustomerId?id="+sharedPreferences.getString("customer_id", "").trim());

        new GetPostClass("http://admin.trainpanda.com:80/api/pnrs/getByCustomerId?id="+sharedPreferences.getString("customer_id", "").trim() , EnumType.GET) {
            @Override
            public void response(String response) {
                pb.dismiss();
                Log.e("response ", response);

                try {

                    objPnr =  new GsonBuilder().create().fromJson(response.toString(), FETCH_PNR.class);



                    final ArrayList<FETCH_SUB_PNR> unique = removeDuplicateFromList(objPnr.pnrs);

                    for (FETCH_SUB_PNR element : unique) {
                        Log.e("values", element.pnr);
                    }


                    MyAdapter adp = new MyAdapter(unique,getActivity());
                    pnrList.setAdapter(adp);



                    pnrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            processUpdatePNR(unique.get(i).pnr);
                        }
                    });



                } catch (Exception e) {
                    Log.e("exc", e.toString());
                }

            }

            @Override
            public void error(String error) {
                pb.dismiss();
//                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }.call2();
    }

    private void processUpdatePNR(String smsPnr){


        String url = "http://api.railwayapi.com/pnr_status/pnr/" + smsPnr + "/apikey/" + getActivity().getResources().getString(R.string.key1) + "/";
        Log.e("Url : ", "" + url);
        new PNRAsync().execute(url);

    }


    public ArrayList<FETCH_SUB_PNR> removeDuplicateFromList(ArrayList<FETCH_SUB_PNR> list){
        int s=0;
        ArrayList<FETCH_SUB_PNR> users=new ArrayList<FETCH_SUB_PNR>();
         for(FETCH_SUB_PNR us1 :list){
            for(FETCH_SUB_PNR us2:users){
                if(us1.pnr.equalsIgnoreCase(us2.pnr)){
                    s=1;
                }else{
                    s=0;
                }

            }   if(s==0){
                users.add(us1);
            }

        }
        return users;
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
                        //  saveDataToServer();


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



    void pringMessage(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    class MyAdapter extends BaseAdapter{
        private Context context;
        ArrayList<FETCH_SUB_PNR> valuesPNR;

        MyAdapter(ArrayList<FETCH_SUB_PNR> obj,Context ctx){
            this.context = ctx;
            this.valuesPNR =  obj;
        }

        @Override
        public int getCount() {
            return valuesPNR.size();
        }

        @Override
        public Object getItem(int i) {
            return valuesPNR.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.single_search_pnr_list_item, parent,false);;

            TextView pnrName = (TextView)row.findViewById(R.id.txtPNRNUmber);
            TextView pnrDate = (TextView)row.findViewById(R.id.pnrDate);
            TextView pnrStatus = (TextView)row.findViewById(R.id.pnrStatus);

            pnrName.setTypeface(PrefUtils.getTypeFace(getActivity()));
            pnrDate.setTypeface(PrefUtils.getTypeFace(getActivity()));
            pnrStatus.setTypeface(PrefUtils.getTypeFace(getActivity()));


            pnrName.setText(valuesPNR.get(position).pnr);



            String tempDate = valuesPNR.get(position).startDate;
            String TrimmedDate = tempDate.substring(0,valuesPNR.get(position).startDate.indexOf("T"));

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date doj = sdf.parse(TrimmedDate);

                pnrDate.setText(""+new SimpleDateFormat("dd-MMM-yyyy").format(doj));

            }catch (Exception e){
                Log.e("exc in date conv",e.toString());
            }



            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date doj = sdf.parse(TrimmedDate);

                Date nowTime = new Date();
                if (nowTime.after(doj)) {
                    pnrStatus.setText("COMPLETED");
                } else if (nowTime.before(doj)) {
                    pnrStatus.setText("NOT STARTED");
                } else if (nowTime.equals(doj)) {
                    pnrStatus.setText("COMPLETED");
                }
            }catch (Exception e){
                Log.e("---- EXCe", "" + e.toString());

            }

            return row;
        }
    }


//end of main class
}
