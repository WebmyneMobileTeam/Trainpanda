package com.hackaholic.trainpanda.ui.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
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
    private SharedPreferences sharedPreferences;
    ListView pnrList;

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



                    ArrayList<FETCH_SUB_PNR> unique = removeDuplicateFromList(objPnr.pnrs);

                    for (FETCH_SUB_PNR element : unique) {
                        Log.e("values", element.pnr);
                    }


                    MyAdapter adp = new MyAdapter(unique,getActivity());
                    pnrList.setAdapter(adp);

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

            pnrName.setText(valuesPNR.get(position).pnr);

            pnrStatus.setText(valuesPNR.get(position).currentBookingStatus);

            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM  yyyy hh:mm:ss  z");
                String dateInString = valuesPNR.get(position).startDate;
                final Date doj = sdf.parse(dateInString);

                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String DOJinString = sdf.format(doj);

                pnrDate.setText(DOJinString);

            }catch (Exception e){
                Log.e("exc in date conv",e.toString());
            }


            //Date Conversion starts
            /*

            SimpleDateFormat orgFormat = new SimpleDateFormat("dd-M-yyyy");
            String dateInString = cuurentPNR.doj;

            final Date doj = orgFormat.parse(dateInString);
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM  yyyy hh:mm:ss  z");

            // Give it to me in GMT time.
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String DOJinString = sdf.format(doj);


            pnrDate.setText(valuesPNR.pnrs.get(position).startDate);*/
//Date Conversion ends

            return row;
        }
    }


//end of main class
}
