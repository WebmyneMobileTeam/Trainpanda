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

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(PlaceOrder.this, "user_pref", 0);
        valuesRestraunt = complexPreferences.getObject("current_restraunt", Restraunt.class);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        callWebServices();


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

    void callWebServices(){
        String MainCategory = API.BASE_URL+"categories";
        String SubCategory = API.BASE_URL+"subcategories";
        String MenuItemLink = API.BASE_URL+"items?filter[where][restaurantId]=54fdba259438acb320eaa747";

        progressDialog1 =new ProgressDialog(PlaceOrder.this);
        progressDialog2 =new ProgressDialog(PlaceOrder.this);
        progressDialog3 =new ProgressDialog(PlaceOrder.this);

        processFetchDetails(MainCategory,progressDialog1,1);
        processFetchDetails(SubCategory, progressDialog2, 2);
        processFetchDetails(MenuItemLink, progressDialog3, 3);

        fillupdetails();





    }

    private void setAdapter(){
        checkTypeList = new ArrayList<>();

        checkTypeList.add(new CategoryCell("NothIndian",true,false,false));

        checkTypeList.add(new SubCategoryCell("Desert","Price","Qty"));
        checkTypeList.add(new MenuItemCell("Bverges", "o", "o"));

        LayoutInflater inflater = getLayoutInflater();


        for(int i=0;i<catg.Categories.size();i++) {

            View  mainCatg= inflater.inflate(R.layout.rest_catg_item, ListItemLinear, false);
          /*  View  subCatg= inflater.inflate(R.layout.rest_subcatg_item, ListItemLinear, false);
            View  mainItem= inflater.inflate(R.layout.rest_menu_item, ListItemLinear, false);
*/
            ListItemLinear.addView(mainCatg);

            TextView title = (TextView)mainCatg.findViewById(R.id.title);
            title.setText(catg.Categories.get(i).name);

            for(int j=0;j<subCatg.SubCategories.size();j++){

                        if(subCatg.SubCategories.get(j).categoryId.equalsIgnoreCase(catg.Categories.get(i).id)){

                             View  subCatg2= inflater.inflate(R.layout.rest_subcatg_item, ListItemLinear, false);

                            ListItemLinear.addView(subCatg2);


                            TextView title2 = (TextView)subCatg2.findViewById(R.id.subtitle);
                            title2.setText(subCatg.SubCategories.get(j).name);


                            for(int k=0;k<menuItems.menuItem.size();k++){
                                    if(menuItems.menuItem.get(k).subCategoryId.equalsIgnoreCase(subCatg.SubCategories.get(j).id)) {

                                        View mainItem = inflater.inflate(R.layout.rest_menu_item, ListItemLinear, false);
                                        ListItemLinear.addView(mainItem);

                                        TextView itemName = (TextView)mainItem.findViewById(R.id.title);
                                        TextView price = (TextView)mainItem.findViewById(R.id.title2);

                                        TextView minus = (TextView)mainItem.findViewById(R.id.minus);
                                        TextView plus = (TextView)mainItem.findViewById(R.id.plus);
                                        final TextView value = (TextView)mainItem.findViewById(R.id.value);

                                        itemName.setText(menuItems.menuItem.get(k).menuItem);
                                        price.setText("Rs " + menuItems.menuItem.get(k).listPrice);

                                        value.setText(""+counter);


                                        minus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (counter <= 0) {
                                                    counter = 0;
                                                    value.setText(""+counter);
                                                } else {
                                                    counter -= 1;
                                                    value.setText("" + counter);
                                                }


                                            }
                                        });

                                        plus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (counter >= 9) {
                                                    counter = 9;
                                                    value.setText(""+counter);
                                                }
                                                else {
                                                    counter += 1;
                                                    value.setText("" + counter);
                                                }
                                            }
                                        });




                                    }
                            }


                    }
            }




          /*  ListItemLinear.addView(subCatg);
            ListItemLinear.addView(mainItem);*/
        }



       // resAdapter = new RestrauntMenuAdapter(RestrauntDetail.this, checkTypeList);
      //  listviewMenu.setAdapter(resAdapter);
    }


    private void fillupdetails(){

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



    void processFetchDetails(String link,final ProgressDialog pb,final int responseId) {


        pb.setMessage("Loading details...");
        pb.show();

        new GetPostClass(link , EnumType.GET) {
            @Override
            public void response(String response) {
                pb.dismiss();
                //Log.e("response "+responseId, response);

                try {

                    JSONObject mainObject = new JSONObject();
                    JSONArray subArray = new JSONArray();
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()>0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.e("resturnr list", jsonObject.toString());
                            subArray.put(i, jsonObject);
                        }



                        switch (responseId){
                            case 1:
                                mainObject.put("Categories", subArray);
                                Log.e("response "+responseId, mainObject.toString());

                                catg = new GsonBuilder().create().fromJson(mainObject.toString(), TopCategories.class);
                                break;
                            case 2:
                                mainObject.put("SubCategories", subArray);
                                Log.e("response "+responseId, mainObject.toString());

                                subCatg = new GsonBuilder().create().fromJson(mainObject.toString(), TopSubCategories.class);
                                break;
                            case 3:
                                mainObject.put("MenuItems", subArray);
                                Log.e("response "+responseId, mainObject.toString());

                                menuItems = new GsonBuilder().create().fromJson(mainObject.toString(), TopMenuItems.class);
                                setAdapter();
                                break;
                        }


                    }else {
                        Log.e("no record found","in "+responseId);
                    }





                } catch (Exception e) {
                    Log.e("exc", e.toString());
                }

            }

            @Override
            public void error(String error) {
                pb.dismiss();
                Toast.makeText(PlaceOrder.this, error, Toast.LENGTH_SHORT).show();
            }
        }.call2();
    }
}

