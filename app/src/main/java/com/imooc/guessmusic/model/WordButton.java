package com.imooc.guessmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 *
 * Created by Administrator on 2016/5/4 0004.
 */
public class WordButton {

    public int mIndex;
    public boolean mIsVisiable;
    public String mWordString;

    public Button mViewButton;

    public WordButton() {
        mIsVisiable = true;
        mWordString = "";
    }
}
