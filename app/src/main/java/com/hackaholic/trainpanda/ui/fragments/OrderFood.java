package com.hackaholic.trainpanda.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackaholic.trainpanda.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFood extends Fragment {

    EditText etPNR;
    Button buttonGO,buttonPNRSMS;

    public OrderFood() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_food, container, false);

        etPNR = (EditText)view.findViewById(R.id.etPNR);
        buttonGO = (Button)view.findViewById(R.id.buttonGO);
      //  buttonPNRSMS = (Button)view.findViewById(R.id.buttonPNRSMS);

       /* buttonGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etPNR.getText().toString().trim().length()!=0){
                        processFetchPNR();
                }else{
                    Toast.makeText(getActivity(),"Please Enter PNR",Toast.LENGTH_LONG).show();
                }
            }
        });*/

        return view;
    }


    private void processFetchPNR(){

    }

}
