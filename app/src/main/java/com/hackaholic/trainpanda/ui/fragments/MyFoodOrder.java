package com.hackaholic.trainpanda.ui.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.utility.CustomDialogRating;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.FETCH_ORDER;
import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;
import Model.MainRating;
import Model.Restraunt;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFoodOrder extends Fragment {
    ProgressDialog pb,pb2,pb3;
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

                    ProgressDialog pb =  new ProgressDialog(getActivity());

                    fetchRestrauntRatings(pb);

                    /*for(int i=0;i<objOrder.orders.size();i++) {

                        ProgressDialog pb3 =  new ProgressDialog(getActivity());
                        fetchRestrauntDetails(pb3,objOrder.orders.get(i).restaurantId,objOrder.orders.size(),i);
                    }*/

                   /* MyAdapter adp = new MyAdapter(objOrder,getActivity());
                    foodorderList.setAdapter(adp);*/


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


    private void fetchRestrauntRatings(final ProgressDialog pbrating){
        pbrating.setMessage("Loading details...");
        pbrating.show();

        Log.e("fetch pnr link ", "http://admin.trainpanda.com:80/api/restaurantRatings?filter[userId]="+sharedPreferences.getString("customer_id", "").trim());

        new GetPostClass("http://admin.trainpanda.com:80/api/restaurantRatings?filter[userId]="+sharedPreferences.getString("customer_id", "").trim() , EnumType.GET) {
            @Override
            public void response(String response) {
                pbrating.dismiss();
                Log.e("rating response ", response);

                try {

                    JSONObject mainObj = new JSONObject();
                    JSONArray jsonArray=new JSONArray(response);
                    mainObj.put("Rating",jsonArray);

                    MainRating currentRating =  new GsonBuilder().create().fromJson(mainObj.toString(), MainRating.class);

                    for(int i=0;i<objOrder.orders.size();i++) {

                        for (int j = 0; j < currentRating.Rating.size(); j++) {
                            if (objOrder.orders.get(i).restaurantId.equalsIgnoreCase(currentRating.Rating.get(j).restaurantId)) {
                                int cuurentRating = currentRating.Rating.get(j).rating;
                                objOrder.orders.get(i).restaurantRating = String.valueOf(cuurentRating);
                            }
                        }
                    }


                    MyAdapter adp = new MyAdapter(objOrder,getActivity());
                    foodorderList.setAdapter(adp);



                } catch (Exception e) {

                    Log.e("exc", e.toString());
                }

            }

            @Override
            public void error(String error) {
                pbrating.dismiss();
//                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }.call2();
    }


   private void  fetchRestrauntDetails(final ProgressDialog pb3,String restID, final int totalSize,final int cuurrentSize){

      //  pb2 =new ProgressDialog(getActivity());
        pb3.setMessage("Loading details...");
        pb3.show();


        String url= API.BASE_URL+"items?filter[where][restaurantId]="+restID;
        Log.e("link",url);

        new GetPostClass(url, EnumType.GET) {
            @Override
            public void response(String response) {
                pb3.dismiss();

                Log.e("####restraunt data "+cuurrentSize, response);

               /* if(totalSize==cuurrentSize){
                    pb2.dismiss();
                }*/

                /*try {

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



                       if(totalSize==cuurrentSize){

                        }else {
                            MyAdapter adp = new MyAdapter(objOrder, getActivity());
                            foodorderList.setAdapter(adp);
                        }


                    }





                } catch (Exception e) {
                    Log.e("exc", e.toString());
                }*/

            }

            @Override
            public void error(String error) {
                pb3.dismiss();
//                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        }.call2();
    }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);


            View row=convertView;
            MyHolder holder;

            if(row==null){
                holder=new MyHolder();
                row = inflater.inflate(R.layout.single_my_order_list_item, parent,false);
                holder.txtHotelName = (TextView)row.findViewById(R.id.txtHotelName);
                holder.txtDate = (TextView)row.findViewById(R.id.txtDate);
                holder.txtPrice = (TextView)row.findViewById(R.id.txtPrice);
                holder.txtStaionName = (TextView)row.findViewById(R.id.txtStaionName);

                holder.txtFoodType = (TextView)row.findViewById(R.id.txtFoodType);
                holder.txtRating = (TextView)row.findViewById(R.id.txtRating);
                holder.txtRatingBox = (TextView)row.findViewById(R.id.txtRatingBox);


                holder.txtHotelName.setTypeface(PrefUtils.getTypeFace(getActivity()));
                holder.txtStaionName.setTypeface(PrefUtils.getTypeFace(getActivity()));
                holder.txtDate.setTypeface(PrefUtils.getTypeFace(getActivity()));
                holder.txtPrice.setTypeface(PrefUtils.getTypeFace(getActivity()));
                holder.txtRating.setTypeface(PrefUtils.getTypeFace(getActivity()));
                holder.txtRatingBox.setTypeface(PrefUtils.getTypeFace(getActivity()));

                row.setTag(holder);

            }else{
                holder=(MyHolder) row.getTag();
            }



            holder.txtHotelName.setText(""+valuesOrder.orders.get(position).restaurantId);
            holder.txtStaionName.setText("Station - "+valuesOrder.orders.get(position).stationCode);

            String tempDate = valuesOrder.orders.get(position).date;
            String TrimmedDate = tempDate.substring(0,valuesOrder.orders.get(position).date.indexOf("T"));


            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date newdate = sdf.parse(TrimmedDate);

                holder.txtDate.setText(""+new SimpleDateFormat("dd-MMM-yyyy").format(newdate));

            }catch (Exception e){
                Log.e("exc in date conv",e.toString());
            }


            holder.txtPrice.setText("INR "+valuesOrder.orders.get(position).totalAmount);

            //txtFoodType.setText(""+valuesOrder.orders.get(position).restaurantId);
            holder.txtRating.setText("GIVE RATING");


            if(valuesOrder.orders.get(position).restaurantRating == null){
                holder.txtRating.setText("GIVE RATING");
                holder.txtRatingBox.setVisibility(View.GONE);
            }else{
                holder.txtRating.setText("RATED");
                holder.txtRating.setTextColor(Color.WHITE);
                holder.txtRatingBox.setVisibility(View.VISIBLE);
                holder.txtRatingBox.setText(""+valuesOrder.orders.get(position).restaurantRating);
            }



            holder.txtRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(valuesOrder.orders.get(position).restaurantRating == null) {
                        //String hotelName = valuesOrder.orders.get(position).restaurantName;
                        String hotelName = "Hotel Kunal - static";
                        String stCode = valuesOrder.orders.get(position).stationCode;

                        String restID = valuesOrder.orders.get(position).restaurantId;
                        String userId = valuesOrder.orders.get(position).customerId;
                        String ordeID = valuesOrder.orders.get(position).id;

                        CustomDialogRating box = new CustomDialogRating(hotelName,stCode,restID,userId,ordeID,getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                        box.show();
                    }else{
                        Toast.makeText(context,"You have already rated for this order !!!",Toast.LENGTH_SHORT).show();
                    }


                }
            });





            return row;
        }
    }

    public static class MyHolder{
        TextView txtHotelName;
        TextView txtDate;
        TextView txtPrice;
        TextView txtStaionName;
        TextView txtFoodType;
        TextView txtRating;
        TextView txtRatingBox;
    }

//end of main class
}
