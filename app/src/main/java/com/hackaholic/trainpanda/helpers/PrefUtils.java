package com.hackaholic.trainpanda.helpers;

import android.content.Context;
import android.graphics.Typeface;

import com.hackaholic.trainpanda.custom.ComplexPreferences;

import Model.RecentOrder;


/**
 * Created by xitij on 17-03-2015.
 */
public class PrefUtils {


    public static void setRecentOrder(Context ctx,boolean code){
        Prefs.with(ctx).save("isRecentOrder",code);
    }

    public static boolean isRecentOrder(Context ctx){
        boolean ans = Prefs.with(ctx).getBoolean("isRecentOrder", false);
        return ans;
    }



    public static Typeface getTypeFace(Context ctx){
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Regular.ttf");
        return  typeface;
    }


    public static void setCurrentStationCode(Context ctx,String code){
        Prefs.with(ctx).save("stCode",code);
    }

    public static void setTab(Context ctx,String code){
        Prefs.with(ctx).save("Tab",code);
    }

    public static String getTab(Context ctx,String code){
        String tab = Prefs.with(ctx).getString("Tab", "");
        return tab;
    }

    public static String getCurrentStationCode(Context ctx){
        String code = Prefs.with(ctx).getString("stCode", "");
        return code;
    }


    public static void setCurrentLatitude(Context ctx,String code){
        Prefs.with(ctx).save("Latitude",code);
    }

    public static String getCurrentLatitude(Context ctx){
        String code = Prefs.with(ctx).getString("Latitude", "");
        return code;
    }

    public static void setCurrentLongitude(Context ctx,String code){
        Prefs.with(ctx).save("Longitude",code);
    }

    public static String getCurrentLongitude(Context ctx){
        String code = Prefs.with(ctx).getString("Longitude", "");
        return code;
    }


    public static void setDrawerClick(Context ctx,int pos){
        Prefs.with(ctx).save("pos",pos);
    }

    public static int getDrawerClick(Context ctx){
        int code = Prefs.with(ctx).getInt("pos", 0);
        return code;
    }


    public static void setRecentOrederObject(Context ctx, RecentOrder obj){

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref",0);
        complexPreferences.putObject("recentOrder", obj);
        complexPreferences.commit();

    }

    public static RecentOrder getRecentOrederObject(Context ctx){

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref",0);
        RecentOrder order = complexPreferences.getObject("recentOrder", RecentOrder.class);
        return  order;
    }

}
