package com.imooc.guessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.util.Util;

/**
 * Created by Administrator on 2016/5/8 0008.
 */
public class AppPassView extends Activity {

    private ImageButton btnBack;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.all_pass_view);

        // 隐藏右上角的金币按钮
        FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);

        btnBack = (ImageButton) findViewById(R.id.btn_bar_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.startActivity(AppPassView.this, IndexActivity.class);
            }
        });
    }

}
