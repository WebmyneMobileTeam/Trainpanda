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
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
import com.hackaholic.trainpanda.helpers.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.FETCH_ORDER;
import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;
import Model.Restraunt;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFoodOrder extends Fragment {
    ProgressDialog pb,pb2;
    TextView txtPNRNUmber,pnrDate,pnrStatus;
    FETCH_ORDER objOrder;
    private SharedPreferences sharedPreferences;
    ListView foodorderList;

    public MyFoodOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_food_order, container, false);


        foodorderList = (ListView)view.findViewById(R.id.foodorderList);

        sharedPreferences = getActivity().getSharedPreferences("TrainPanda", getActivity().MODE_PRIVATE);
        Log.e("Customer id : ", sharedPreferences.getString("customer_id", "").trim());



        fetchfoodORder();
        return view;
    }


private void fetchfoodORder(){
    pb =new ProgressDialog(getActivity());
    pb.setMessage("Loading details...");
    pb.show();

    Log.e("fetch pnr link ", "http://admin.trainpanda.com:80/api/orders?filter[id]="+sharedPreferences.getString("customer_id", "").trim());

    new GetPostClass("http://admin.trainpanda.com:80/api/orders?filter[id]="+sharedPreferences.getString("customer_id", "").trim() , EnumType.GET) {
        @Override
        public void response(String response) {
            pb.dismiss();
            Log.e("response ", response);

            try {

                JSONObject mainObject = new JSONObject();
                JSONArray subArray = new JSONArray();

                JSONArray jsonArray=new JSONArray(response);

                if(jsonArray.length()>0)
                {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        subArray.put(i,jsonObject);
                    }

                    mainObject.put("orders", subArray);
                    Log.e("final order list", mainObject.toString());

                    objOrder =  new GsonBuilder().create().fromJson(mainObject.toString(), FETCH_ORDER.class);

                  //  fetchRestrauntDetails();

                    MyAdapter adp = new MyAdapter(objOrder,getActivity());
                    foodorderList.setAdapter(adp);


                }





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



   /* private void  fetchRestrauntDetails(){

        pb2 =new ProgressDialog(getActivity());
        pb2.setMessage("Loading details...");
        pb2.show();


        String url= API.BASE_URL+"items?filter[where][restaurantId]="+objOrder.orders.;
        Log.e("link",url);

        new GetPostClass(url, EnumType.GET) {
            @Override
            public void response(String response) {
                pb2.dismiss();
                Log.e("response ", response);

                try {

                    JSONObject mainObject = new JSONObject();
                    JSONArray subArray = new JSONArray();

                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()>0)
                    {
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            subArray.put(i,jsonObject);
                        }

                        mainObject.put("orders", subArray);
                        Log.e("final order list", mainObject.toString());



                        MyAdapter adp = new MyAdapter(objOrder,getActivity());
                        foodorderList.setAdapter(adp);


                    }





                } catch (Exception e) {
                    Log.e("exc", e.toString());
                }

            }

            @Override
            public void error(String error) {
                pb2.dismiss();
//                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }.call2();
    }
*/
    class MyAdapter extends BaseAdapter {
        private Context context;
        FETCH_ORDER valuesOrder;

        MyAdapter(FETCH_ORDER obj,Context ctx){
            this.context = ctx;
            this.valuesOrder =  obj;
        }

        @Override
        public int getCount() {
            return valuesOrder.orders.size();
        }

        @Override
        public Object getItem(int i) {
            return valuesOrder.orders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.single_my_order_list_item, parent,false);;

            TextView txtHotelName = (TextView)row.findViewById(R.id.txtHotelName);
            TextView txtDate = (TextView)row.findViewById(R.id.txtDate);
            TextView txtPrice = (TextView)row.findViewById(R.id.txtPrice);
            TextView txtStaionName = (TextView)row.findViewById(R.id.txtStaionName);

            TextView txtFoodType = (TextView)row.findViewById(R.id.txtFoodType);
            TextView txtRating = (TextView)row.findViewById(R.id.txtRating);
            TextView txtRatingBox = (TextView)row.findViewById(R.id.txtRatingBox);


            txtHotelName.setText(""+valuesOrder.orders.get(position).restaurantId);
            txtStaionName.setText("Station - "+valuesOrder.orders.get(position).stationCode);

            txtDate.setText(""+valuesOrder.orders.get(position).date);

            txtPrice.setText("INR "+valuesOrder.orders.get(position).totalAmount);

            //txtFoodType.setText(""+valuesOrder.orders.get(position).restaurantId);
            txtRating.setText("GIVE RATING");
            txtRatingBox.setText("4.5");

            return row;
        }
    }


//end of main class
}
