package com.hackaholic.trainpanda.adapter;

/**
 * Created by Daphnis Labs on 02-06-2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackaholic.trainpanda.Cells.CellType;
import com.hackaholic.trainpanda.Cells.CheckType;
import com.hackaholic.trainpanda.R;

import java.util.ArrayList;
import android.util.Log;

public class RestrauntMenuAdapter extends BaseAdapter {

    private final Activity context;
    private ArrayList<CheckType> checkTypeList;

    public RestrauntMenuAdapter(Activity context,ArrayList<CheckType> value) {
        this.context = context;
        this.checkTypeList = value;

        Log.e("chk list size",""+checkTypeList.size());

    }

    @Override
    public int getCount() {
        if (checkTypeList == null) return 0;

        return checkTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        if (checkTypeList == null) return null;
        return checkTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CheckType i = checkTypeList.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View v1 = convertView;

        v1= inflater.inflate(R.layout.rest_catg_item, null, true);
       // View v2 = convertView;

        /*if(i.isCategory()){
                final CellType cellButton=(CellType)i;
                 v1= inflater.inflate(R.layout.rest_catg_item, null, true);

            return v1;
        }else if(i.isSubCategory()){
             v1= inflater.inflate(R.layout.rest_subcatg_item, null, true);

            return v1;
        }else if(i.isMenuItem()){
             v1= inflater.inflate(R.layout.rest_menu_item, null, true);

            return v1;
        }
*/


       return v1;
    }
}