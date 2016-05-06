package com.imooc.guessmusic.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.Song;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.MyLog;
import com.imooc.guessmusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,IWordButtonClickListener{

    // 唱片相关动画
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    // PLAY 按键事件
    private ImageButton mBtnPlayStart;

    private ImageView mViewPan;
    private ImageView mViewPanBar;

    private boolean mIsRunning = false;

    // word
    public final static int COUNT_WORDS = 24;
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mBtnSelectWords;
    private MyGridView mMyGridView;

    // selected
    private LinearLayout mViewWordsContainer;

    // current song
    private Song mCurrentSong;

    // current stage index
    private int mCurrentStageIndex = -1;

    private static final String TAG = "MainActivity";

    /**
     *  ANSWER state
     */
    public final static int STATUS_ANSWER_RIGHT = 1;
    public final static int STATUS_ANSWER_WRONG = 2;
    public final static int STATUS_ANSWER_LACK = 3;

    // shanshuo number
    public final static int SPARKTIME = 6;

    // pass layout
    private View mPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化动画
        initAnim();
        // 初始化控件
        initView();
        // 初始化文字数据
        initCurrentStageData();

    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_start:
                handlePlayButton();
        }
    }

    /**
     * 处理圆盘中间的播放按钮，就是开始播放音乐
     */
    private void handlePlayButton() {
        if (mViewPanBar != null) {
            if (!mIsRunning) {
                mIsRunning = true;

                // 开始拨杆进入动画
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(this);

        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);

        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        // 注册监听
        mMyGridView.registOnWordButtonClick(this);

        mViewWordsContainer = (LinearLayout) findViewById(
                R.id.word_select_container);
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        // 转盘
        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 转盘转动结束，拨杆推开
                mViewPanBar.startAnimation(mBarOutAnim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 拨杆进来
        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 拨杆进入，转盘转动
                mViewPan.startAnimation(mPanAnim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // 拨杆出去
        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // 整套动画播放完毕
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    protected void onPause() {
        // 清除动画
        mViewPan.clearAnimation();
        super.onPause();
    }

    /**
     *
     */
    private Song loadStageSongInfo(int stageIndex) {
        Song song = new Song();
        String[] stage = Const.SONG_INFQ[stageIndex];
        song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
        song.setSongName(stage[Const.INDEX_SONG_NAME]);

        return song;
    }
    /**
     * 当前关卡的数据
     */
    private void initCurrentStageData() {
        // read current stage song info
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
        // initial selected
        mBtnSelectWords = initWordSelect();

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(140, 140);
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            mViewWordsContainer.addView(
                    mBtnSelectWords.get(i).mViewButton,
                    params);
        }

        // get data
        mAllWords = initAllWord();
        // updata data - MyGridView
        mMyGridView.updataData(mAllWords);
    }

    /**
     * 初始待选文字数据
     * @return 24个按钮的文字数据
     */
    private ArrayList<WordButton> initAllWord() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        // get all word
        String[] words = generateWords();

        for (int i = 0; i < COUNT_WORDS; i++) {
            WordButton button = new WordButton();

            button.mWordString = words[i];

            data.add(button);
        }

        return data;
    }

    /**
     * 初始化已选择文字框
     */
    private ArrayList<WordButton> initWordSelect() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            View view = Util.getView(MainActivity.this,
                    R.layout.self_ui_gridview_item);

            final WordButton holder = new WordButton();

            holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
            holder.mViewButton.setTextColor(Color.WHITE);
            holder.mViewButton.setText("");
            holder.mIsVisiable = false;
            holder.mViewButton.setBackgroundResource(R.mipmap.game_wordblank);
            holder.mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearTheAnswer(holder);
                }
            });
            data.add(holder);
        }

        return data;
    }

    /**
     * right
     * wrong
     * lack
     */
    @Override
    public void onWordButtonClick(WordButton wordButton) {
        setSelectWord(wordButton);

        // answer state
        int checkResult = checkTheAnswer();

        // check answer
        if (checkResult == STATUS_ANSWER_RIGHT) {
            // get reward & pass this stage
            handlePassEvent();
        } else if (checkResult == STATUS_ANSWER_WRONG) {
            // wrong mention
            sparkWords();

        } else if (checkResult == STATUS_ANSWER_LACK) {
            // normal
            for (int i = 0; i < mBtnSelectWords.size(); i++) {
                mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
            }
        }
    }

    /**
     * clear answer
     */
    private void clearTheAnswer(WordButton wordButton) {
        wordButton.mViewButton.setText("");
        wordButton.mWordString = "";
        wordButton.mIsVisiable = false;

        // set daixuan
        setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
    }
    /**
     * set answer
     * @param wordButton
     */
    private void setSelectWord(WordButton wordButton) {
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {
                // set answer text content
                mBtnSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
                mBtnSelectWords.get(i).mIsVisiable = true;
                mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
                // record index
                mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

                // Log ...
                MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

                // set daixuan visibility
                setButtonVisiable(wordButton, View.INVISIBLE);

                break;
            }
        }
    }

    /**
     * daixuan text
     */
    private void setButtonVisiable(WordButton button, int visibility) {
        button.mViewButton.setVisibility(visibility);
        button.mIsVisiable = (visibility == View.VISIBLE) ?
                true : false;
        MyLog.d(TAG, button.mIsVisiable + "");
    }

    /**
     * generate all words
     * @return
     */
    private String[] generateWords() {

        Random random = new Random();

        String[] words =new String[COUNT_WORDS];

        // save song name
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }

        // save random character left
        for (int i = mCurrentSong.getNameLength();
             i < COUNT_WORDS; i++) {
            words[i] = getRandomChar() + "";
        }

        // shuffled: first, select a random exchange with 1
        // next, select a random exchange with 2
        // until the end
        for (int i = COUNT_WORDS - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);
            String buf = words[index];
            words[index] = words[i];
            words[i]= buf;
        }


        return words;
    }

    /**
     * generate random char
     * @return
     */
    private char getRandomChar() {
        String str = "";
        int highPos;
        int lowPos;

        Random random = new Random();

        highPos = (176 + Math.abs(random.nextInt(30)));
        lowPos = (161 + Math.abs(random.nextInt(50)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(highPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str.charAt(0);
    }

    /**
     * chech answer
     */
    private int checkTheAnswer() {
        // answer length
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            // if has empty word, LACK
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {
                return STATUS_ANSWER_LACK;
            }
        }

        // answer right
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            sb.append(mBtnSelectWords.get(i).mWordString);
        }

        return (sb.toString().equals(mCurrentSong.getSongName())
                ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG);
    }

    /**
     * shanshuo word
     */
    private void sparkWords() {
        // timer
        TimerTask task = new TimerTask() {
            boolean mChange = false;
            int mSpardTimes = 0;

            @Override
            public void run() {
                //
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // show
                        if (++mSpardTimes > SPARKTIME) {
                            return;
                        }

                        // red white
                        for (int i = 0; i < mBtnSelectWords.size(); i++) {
                            mBtnSelectWords.get(i).mViewButton.setTextColor(
                                    mChange ? Color.RED : Color.WHITE);
                        }

                        mChange = !mChange;
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    /**
     * pass
     */
    private void handlePassEvent() {
        mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
        mPassView.setVisibility(View.VISIBLE);
    }


}
