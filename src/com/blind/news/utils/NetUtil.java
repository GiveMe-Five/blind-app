package com.blind.news.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by DELL on 2015/2/21.
 */
public class NetUtil {

    public static boolean NetWorkStatus(Context context){
        boolean netStatus = false;
        ConnectivityManager cntManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cntManager.getActiveNetworkInfo()!=null){
            netStatus = cntManager.getActiveNetworkInfo().isAvailable();
        }
        if (cntManager.getActiveNetworkInfo() == null || !netStatus){
            Toast msg = Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT);
            msg.show();
        }
        return netStatus;
    }



}
