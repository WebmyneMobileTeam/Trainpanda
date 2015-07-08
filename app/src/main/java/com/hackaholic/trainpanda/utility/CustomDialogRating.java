package com.hackaholic.trainpanda.utility;

import android.app.Activity;
import android.app.Dialog;

import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.RatingCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.JSONPost;
import com.hackaholic.trainpanda.helpers.POSTResponseListener;
import com.hackaholic.trainpanda.helpers.PrefUtils;
import com.hackaholic.trainpanda.ui.fragments.MyFoodOrder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Android on 27-04-2015.
 */
public class CustomDialogRating extends Dialog {
    private ProgressDialog progressDialog;
    FragmentActivity act;
    String RESTID,USERID,ORDERID,HotelName,StationName;
    TextView txtHotelName,txtStaionName,txtClose,txtUpdate,txtTiltle,txtClose1;
    RatingBar ratingBar;
    int RATING;
    public CustomDialogRating(Activity context) {
        super(context);



    }

    public CustomDialogRating(String hname,String stName,String restid,String userid,String order,FragmentActivity context, int theme) {
        super(context, theme);
        this.act = context;
        this.HotelName = hname;
        this.StationName = stName;
        this.RESTID = restid;
        this.USERID = userid;
        this.ORDERID = order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_rating);

        txtTiltle= (TextView)findViewById(R.id.txtHotelName);
        txtHotelName = (TextView)findViewById(R.id.txtHotelName);
        txtStaionName = (TextView)findViewById(R.id.txtStaionName);
        txtUpdate = (TextView)findViewById(R.id.txtUpdate);
        txtClose = (TextView)findViewById(R.id.txtClose);
        txtClose1 = (TextView)findViewById(R.id.txtClose1);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        txtHotelName.setText(HotelName);
        txtStaionName.setText("Station - " + StationName);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                RATING = (int) v;
            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RATING == 0) {
                    Toast.makeText(act, "Please give some rating !!!", Toast.LENGTH_SHORT).show();
                } else {
                    processGIVERATING();
                }


            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

        //For refreshing the update rated data
        txtClose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                MyFoodOrder fragment = new MyFoodOrder();
                FragmentManager fragmentManager22 = act.getSupportFragmentManager();
                fragmentManager22.beginTransaction().replace(R.id.lk_profile_fragment, fragment).commit();
            }
        });


    }

    private  void processGIVERATING(){

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("restaurantId",RESTID);
            jsonObject.put("customerId", USERID);
            jsonObject.put("orderId",ORDERID );
            jsonObject.put("feedback", "test");
            jsonObject.put("rating",RATING);


            Log.e("FINAL rating JSON : ", "" + jsonObject);


                JSONPost json1 = new JSONPost();
                json1.POST(act, "http://admin.trainpanda.com/api/orders", jsonObject.toString(), "Submitting your rating...");
                json1.setPostResponseListener(new POSTResponseListener() {
                @Override
                    public String onPost(String msg) {

                    Log.e("rating res", msg);
                        txtTiltle.setText("Thank You");
                        txtHotelName.setVisibility(View.GONE);
                        txtStaionName.setText("Thank you for submitting your rating. We value your feedback.");
                        txtUpdate.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.GONE);

                        txtClose.setVisibility(View.GONE);
                        txtClose1.setVisibility(View.VISIBLE);

                        return null;
                    }

                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onBackground() {

                    }
                });

        } catch (Exception e) {
            Log.e("--- exc",e.toString());
            e.printStackTrace();
        }


    }



    //end of main class
}
