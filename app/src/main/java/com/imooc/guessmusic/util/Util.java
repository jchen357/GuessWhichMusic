package com.imooc.guessmusic.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class Util {

    public static View getView(Context context, int layoutid) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(layoutid, null);

        return layout;
    }

    /**
     * 界面跳转
     */
    public static void startActivity(Context context, Class desti) {
        Intent intent = new Intent();
        intent.setClass(context, desti);
        context.startActivity(intent);

        // 关闭当前的ACTIVITY
        ((Activity)context).finish();
    }
}
