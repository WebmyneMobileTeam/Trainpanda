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

}
