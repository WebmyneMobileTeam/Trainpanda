package com.hackaholic.trainpanda.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.ServiceHandler.ServiceHandler;
import com.hackaholic.trainpanda.custom.ComplexPreferences;
import com.hackaholic.trainpanda.ui.fragments.TrainRoutesFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.FETCH_PNR;
import Model.FETCH_SUB_PNR;
import Model.PNR;

/**
 * Created by Android on 27-04-2015.
 */
public class CustomDialogPNRList extends Dialog {
    Activity act;
    public Dialog d;
    ListView pnrList;

    public CustomDialogPNRList(Activity context) {
        super(context);



    }

    public CustomDialogPNRList(Activity context, int theme) {
        super(context, theme);
        this.act = context;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pnr_list);

       /* ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(act, "user_pref", 0);
        FETCH_PNR pnrobj = complexPreferences.getObject("sms", FETCH_PNR.class);
*/
        FETCH_PNR pnrobj = new FETCH_PNR();


        pnrList = (ListView)findViewById(R.id.pnrList);
        MyAdapter adp = new MyAdapter(pnrobj,act);
        pnrList.setAdapter(adp);


        pnrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(act,"Clicked - "+i,Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }


    class MyAdapter extends BaseAdapter {
        private Context context;
        FETCH_PNR valuesPNR;

        MyAdapter(FETCH_PNR obj,Context ctx){
            this.context = ctx;
            this.valuesPNR =  obj;
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.pnr_list_item, parent,false);;

            TextView pnrNo = (TextView)row.findViewById(R.id.pnrNumber);

            pnrNo.setText("75757575");


            return row;
        }
    }


    //end of main class
}
