package com.hackaholic.trainpanda.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeorder);
        initViews();

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

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ tv_restaurant_mobile.getText().toString()));
                startActivity(callIntent);
            }
        });

        imgSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("phoneNo", null, "sms message", null, null);*/

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", new String(tv_restaurant_mobile.getText().toString()));
                smsIntent.putExtra("sms_body"  , "Test SMS to Angilla");
                startActivity(smsIntent);
            }
        });

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

        txtHotelName.setText(valuesRestraunt.Restraunt.get(listPosition).name);
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

