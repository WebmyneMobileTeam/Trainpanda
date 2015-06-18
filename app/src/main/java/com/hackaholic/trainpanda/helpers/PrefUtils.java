package com.hackaholic.trainpanda.helpers;

import android.content.Context;
import android.graphics.Typeface;


/**
 * Created by xitij on 17-03-2015.
 */
public class PrefUtils {

    public static void setCurrentStationCode(Context ctx,String code){
        Prefs.with(ctx).save("stCode",code);
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

}
