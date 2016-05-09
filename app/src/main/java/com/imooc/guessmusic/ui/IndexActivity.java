package com.imooc.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.util.Util;

/**
 * Created by Administrator on 2016/5/9 0009.
 */
public class IndexActivity extends Activity implements View.OnClickListener{

    private ImageButton mBtnAbout;

    private ImageButton mBtnGuessPicture;

    private ImageButton mBtnGuessMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_layout);


        initView();
    }

    private void initView() {
        //初始化ImageButton
        mBtnAbout = (ImageButton) findViewById(R.id.btn_index_about);

        mBtnGuessPicture = (ImageButton) findViewById(R.id.btn_index_caitu);

        mBtnGuessMusic = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnGuessMusic.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_index_about:

                break;

            case R.id.btn_index_caitu:

                break;

            case R.id.btn_play_start:
                Util.startActivity(IndexActivity.this, MainActivity.class);
                break;

            default:

                break;
        }
    }
}
