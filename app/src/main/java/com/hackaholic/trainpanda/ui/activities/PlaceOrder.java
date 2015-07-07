package com.hackaholic.trainpanda.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.Cells.CategoryCell;
import com.hackaholic.trainpanda.Cells.CheckType;
import com.hackaholic.trainpanda.Cells.MenuItemCell;
import com.hackaholic.trainpanda.Cells.SubCategoryCell;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.adapter.RestrauntMenuAdapter;
import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.helpers.API;
import com.hackaholic.trainpanda.helpers.EnumType;
import com.hackaholic.trainpanda.helpers.GetPostClass;
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.utility.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import Model.RecentOrder;
import Model.Restraunt;
import Model.TopCategories;
import Model.TopMenuItems;
import Model.TopSubCategories;

public class PlaceOrder extends FragmentActivity {
    TextView txtHotelName,menu,time,minOrder,delivery,tv_restaurant_timings,tv_restaurant_mobile;
    LinearLayout nonveg,veg;
    int listPosition;
    ProgressDialog progressDialog1,progressDialog2,progressDialog3;
    Restraunt valuesRestraunt;
    ListView listviewMenu;
    LinearLayout ListItemLinear;
    TopCategories catg;
    TopSubCategories subCatg;
    TopMenuItems menuItems;
    int counter=0;
    RestrauntMenuAdapter resAdapter;
    ArrayList<CheckType> checkTypeList;
    ImageView imgBack,imgCall,imgSms;
    int finalPrice;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeorder);
        initViews();

        sharedPreferences = PlaceOrder.this.getSharedPreferences("TrainPanda", MODE_PRIVATE);

        listPosition = getIntent().getIntExtra("pos", 0);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(PlaceOrder.this, "user_pref", 0);
        valuesRestraunt = complexPreferences.getObject("current_restraunt", Restraunt.class);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        fillupdetails();

        fillupCartdetails();

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               saveDataToServer(0);

            }
        });

        imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              saveDataToServer(1);

             /*   SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);*/

            }
        });

    }


  private void saveDataToServer(final int TODO) {

        try {
            JSONObject customerJsonObject = new JSONObject();
            customerJsonObject.put("id", sharedPreferences.getString("customer_id", "").trim());
            Log.e("Customer Json Object : ", String.valueOf(customerJsonObject));


            final Date currentTime = new Date();

            final SimpleDateFormat sdf =
                    new SimpleDateFormat("EEE, d MMM  yyyy hh:mm:ss  z");

        // Give it to me in GMT time.
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            System.out.println("GMT time: " + sdf.format(currentTime));

            final String todayDate = sdf.format(currentTime);

            Log.e("Date",""+todayDate);


            JSONObject jsonObject = new JSONObject();

            jsonObject.put("orderId", 0);
            jsonObject.put("totalAmount",finalPrice);
            jsonObject.put("customerId", sharedPreferences.getString("customer_id", "").trim());
            jsonObject.put("date",todayDate );
            jsonObject.put("status", 0);
            jsonObject.put("stationCode",valuesRestraunt.Restraunt.get(listPosition).stationCode);
            jsonObject.put("restaurantId",valuesRestraunt.Restraunt.get(listPosition).id);

            Log.e("FINAL ORDER JSON : ", "" + jsonObject);

            try {

                JSONPost json1 = new JSONPost();
                json1.POST(PlaceOrder.this, "http://admin.trainpanda.com/api/orders", jsonObject.toString(), "Saving Order Details...");
                json1.setPostResponseListener(new POSTResponseListener() {
                    @Override
                    public String onPost(String msg) {

                        Log.e("add order to server", "onPost response: " + msg);

                        try {
                            JSONObject jsonObj = new JSONObject(msg);

                            RecentOrder obj = new RecentOrder();

                            obj.RestrauntID = valuesRestraunt.Restraunt.get(listPosition).id;
                            obj.RestrauntName = valuesRestraunt.Restraunt.get(listPosition).name;
                            obj.stationName = valuesRestraunt.Restraunt.get(listPosition).stationCode;
                            obj.OrderAmount = String.valueOf(finalPrice);
                            obj.OrderDate = String.valueOf(todayDate);
                            obj.OrderId = jsonObj.getString("id");

                            PrefUtils.setRecentOrederObject(PlaceOrder.this, obj);

                            PrefUtils.setRecentOrder(PlaceOrder.this, true);

                        }catch (Exception e){
                            Log.e("exc ---",e.toString());
                        }

                        if(TODO ==0){
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+ tv_restaurant_mobile.getText().toString()));
                            startActivity(callIntent);
                        }else {
                            try {
                                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                smsIntent.setData(Uri.parse("smsto:"));
                                smsIntent.setType("vnd.android-dir/mms-sms");
                                smsIntent.putExtra("address", new String(tv_restaurant_mobile.getText().toString()));
                                smsIntent.putExtra("sms_body", "Test SMS to Angilla");
                                startActivity(smsIntent);
                            }catch (Exception e){
                                Toast.makeText(PlaceOrder.this,"Error - "+e.toString(),Toast.LENGTH_LONG).show();
                            }
                        }


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
                Log.e("exc----", e.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    void fillupCartdetails(){
        LayoutInflater inflater = getLayoutInflater();

        //for(int i=0;i<5;i++) {

        SharedPreferences sp = getSharedPreferences("currentOrder", 0);
        int qty = sp.getInt("qty", 0);
        String itemName = sp.getString("itemname", "");
        int price = sp.getInt("price", 0);

        View cartItem = inflater.inflate(R.layout.placorder_catg_item, ListItemLinear, false);

        TextView txtName = (TextView)cartItem.findViewById(R.id.txtName);
        TextView txtqty = (TextView)cartItem.findViewById(R.id.qty);
        TextView txtPrice = (TextView)cartItem.findViewById(R.id.price);


         finalPrice = price * qty;

        txtName.setText(itemName);
        txtqty.setText("x"+qty);
        txtPrice.setText("Rs: "+finalPrice + "/-");

            ListItemLinear.addView(cartItem);
        // }
        View bottom = inflater.inflate(R.layout.placorder_catg_bottom, ListItemLinear, false);
        ListItemLinear.addView(bottom);

        TextView txtTotalPrice = (TextView)bottom.findViewById(R.id.totprice);
        txtTotalPrice.setText("Rs: "+finalPrice+ "/-");
    }



    void initViews(){

        imgCall = (ImageView)findViewById(R.id.imgCall);
        imgSms = (ImageView)findViewById(R.id.imgSms);

        imgBack = (ImageView)findViewById(R.id.imgBack);

        ListItemLinear =(LinearLayout)findViewById(R.id.ListItemLinear);
        //listviewMenu = (ListView)findViewById(R.id.listviewMenu);

        txtHotelName=(TextView)findViewById(R.id.txtHotelName);
        tv_restaurant_mobile=(TextView)findViewById(R.id.mobile);
        menu=(TextView)findViewById(R.id.menu);
        time=(TextView)findViewById(R.id.time);

        minOrder=(TextView)findViewById(R.id.minOrder);
        delivery=(TextView)findViewById(R.id.delivery);
        veg=(LinearLayout)findViewById(R.id.ll_veg_boundry);
        nonveg=(LinearLayout)findViewById(R.id.ll_nonveg_boundry);
    }





    private void fillupdetails() {

        txtHotelName.setText(valuesRestraunt.Restraunt.get(listPosition).name.toUpperCase());
        tv_restaurant_mobile.setText("" + valuesRestraunt.Restraunt.get(listPosition).mobileNo);


        if(valuesRestraunt.Restraunt.get(listPosition).jainFoodAvailable ){
            menu.setText(" " +"JainFood");
        }


        if(valuesRestraunt.Restraunt.get(listPosition).northIndia ){
            menu.setText(" " +"North Indian");
        }


        if(valuesRestraunt.Restraunt.get(listPosition).southIndia ){
            menu.setText(" " +"South Indian");
        }

        if(valuesRestraunt.Restraunt.get(listPosition).pizza ){
            menu.setText(" " +"Pizza");
        }

        time.setText(valuesRestraunt.Restraunt.get(listPosition).morningOpeningTime+" - "+valuesRestraunt.Restraunt.get(listPosition).morningClosingTime + " Hrs , "+valuesRestraunt.Restraunt.get(listPosition).eveningOpeningTime+" - "+valuesRestraunt.Restraunt.get(listPosition).eveningClosingTime+" Hrs");
        minOrder.setText("Min. Order : " + valuesRestraunt.Restraunt.get(listPosition).minOrderAmount+" INR");

        if(valuesRestraunt.Restraunt.get(listPosition).deliveryCharges ==0)
            delivery.setText("Delivery : Free");
        else
            delivery.setText("Delivery : "+valuesRestraunt.Restraunt.get(listPosition).deliveryCharges+" INR");


        if(valuesRestraunt.Restraunt.get(listPosition).vegItems) {
            veg.setVisibility(View.VISIBLE);
            nonveg.setVisibility(View.GONE);
        }
        else{
            veg.setVisibility(View.GONE);
            nonveg.setVisibility(View.VISIBLE);
        }
    }



}

