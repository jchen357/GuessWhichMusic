package com.imooc.guessmusic.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.util.Util;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/4 0004.
 */
public class MyGridView extends GridView{

    private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

    private MyGridAdapter mAdapter;

    private Context mContext;

    // 定义动画
    private Animation mScaleAnimation;

    // 监听gridview
    private IWordButtonClickListener mWordButtonListener;

    // word number
    public final static int COUNTS_WORDS = 24;

    public MyGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mContext = context;

        mAdapter = new MyGridAdapter();
        this.setAdapter(mAdapter);
    }

    /**
     * 文字数据
     */
    public void updataData(ArrayList<WordButton> list) {
        mArrayList = list;

        // 重新设置数据源
        setAdapter(mAdapter);
    }

    class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return mArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final WordButton holder;

            if (view == null) {
                view = Util.getView(mContext,
                        R.layout.self_ui_gridview_item);
                holder = mArrayList.get(i);

                // 加载动画
                mScaleAnimation = AnimationUtils
                        .loadAnimation(mContext, R.anim.scale);
                // 延迟时间
                mScaleAnimation.setStartOffset(i * 100);

                holder.mIndex = i;
                holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
                holder.mViewButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mWordButtonListener.onWordButtonClick(holder);
                    }
                });

                view.setTag(holder);
            } else {
                holder = (WordButton) view.getTag();
            }

            holder.mViewButton.setText(holder.mWordString);

            //播放动画
            view.startAnimation(mScaleAnimation);

            return view;
        }
    }

    /**
     * 注册监听接口
     * @param listener
     */
    public void registOnWordButtonClick(IWordButtonClickListener listener) {
        mWordButtonListener = listener;
    }

}
