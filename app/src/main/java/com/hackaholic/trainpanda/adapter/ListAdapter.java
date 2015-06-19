package com.hackaholic.trainpanda.adapter;

/**
 * Created by Daphnis Labs on 02-06-2015.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackaholic.trainpanda.R;
import com.hackaholic.trainpanda.helpers.PrefUtils;


public class ListAdapter extends ArrayAdapter<String>{


    private final int drawerPos;
    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    public ListAdapter(Activity context,String[] web, Integer[] imageId,int ListPos) {
        super(context, R.layout.list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.drawerPos = ListPos;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(web[position]);
        txtTitle.setTypeface(PrefUtils.getTypeFace(context));

        if(drawerPos==position)
            rowView.setBackgroundColor(Color.parseColor("#8B0001"));

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}