package com.imooc.guessmusic.util;

import android.content.Context;
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
}
