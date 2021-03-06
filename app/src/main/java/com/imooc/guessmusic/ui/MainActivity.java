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
import android.widget.TextView;

import com.imooc.guessmusic.R;
import com.imooc.guessmusic.data.Const;
import com.imooc.guessmusic.model.IAlertDialogButtonListener;
import com.imooc.guessmusic.model.IWordButtonClickListener;
import com.imooc.guessmusic.model.Song;
import com.imooc.guessmusic.model.WordButton;
import com.imooc.guessmusic.myui.MyGridView;
import com.imooc.guessmusic.util.MyLog;
import com.imooc.guessmusic.util.MyPlayer;
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
    private int mCurrentStageIndex = 9;

    private static final String TAG = "MainActivity";

    // current coin number
    private int mCurrentCoins = Const.TOTAL_COINS;

    // coin view
    private TextView mViewCurrentCoins;

    private ImageButton mBtnBack;
    /**

     *  ANSWER state
     */
    public final static int STATUS_ANSWER_RIGHT = 1;
    public final static int STATUS_ANSWER_WRONG = 2;
    public final static int STATUS_ANSWER_LACK = 3;

    // shanshuo number
    public final static int SPARKTIME = 6;

    private final static int ID_DIALOG_DELETE_WORD = 1;

    private final static int ID_DIALOG_TIP_ANSWER = 2;

    private final static int ID_DIALOG_LACK_COINS = 3;

    // pass layout
    private View mPassView;

    // current stage index
    private TextView mCurrentStagePassView;

    private TextView mCurrentStageView;

    private TextView mCurrentSongNamePassView;

    private TextView mCurrentAchievePassView;

    private TextView mCurrentCoinsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 读取数据
        int[] datas = Util.loadData(this);
        mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

        // 初始化动画
        initAnim();
        // 初始化控件
        initView();
        // 初始化文字数据
        initCurrentStageData();


        // 处理删除按键事件
        handleDeleteWord();
        // 处理提示按键事件
        handleTipWord();


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
                break;
            case R.id.btn_bar_back:
                Util.startActivity(MainActivity.this, IndexActivity.class);
                break;

            default:
                break;
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

                // 播放音乐
                MyPlayer.playSong(MainActivity.this,
                        mCurrentSong.getSongFileName());
            }
        }

    }

    /**
     * 初始化控件
     */
    private void initView() {
        mBtnBack = (ImageButton) findViewById(R.id.btn_bar_back);
        mBtnBack.setOnClickListener(this);

        mCurrentCoinsView = (TextView) findViewById(R.id.txt_bar_coins);
        mCurrentCoinsView.setText(mCurrentCoins + "");

        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(this);

        mViewPan = (ImageView) findViewById(R.id.imageView1);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);

        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");

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
        // 保存游戏数据
        Util.saveData(MainActivity.this,
                mCurrentStageIndex - 1, mCurrentCoins);

        // 清除动画
        mViewPan.clearAnimation();

        // 暂停音乐
        MyPlayer.stopTheSong(MainActivity.this);
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

        mCurrentCoinsView = (TextView) findViewById(R.id.txt_bar_coins);
        mCurrentCoinsView.setText(mCurrentCoins + "");

        // read current stage song info
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
        // initial selected
        mBtnSelectWords = initWordSelect();

        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(140, 140);

        // 清空原来的答案
        mViewWordsContainer.removeAllViews();

        // 增加新的答案框
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            mViewWordsContainer.addView(
                    mBtnSelectWords.get(i).mViewButton,
                    params);
        }
        // 显示当前关的索引
        mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
        if (mCurrentStageView != null) {
            mCurrentStageView.setText("" + (mCurrentStageIndex + 1));
        }
        // get data
        mAllWords = initAllWord();
        // updata data - MyGridView
        mMyGridView.updataData(mAllWords);

        // 一开始播放音乐
        handlePlayButton();
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
            mCurrentCoins += 3;

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
        // 显示过关界面
        mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
        mPassView.setVisibility(View.VISIBLE);

        // 停止未完成的动画
        mViewPan.clearAnimation();
        MyPlayer.stopTheSong(MainActivity.this);

        // 播放音效
        MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);

        // 您已成功击败%的玩家
        mCurrentAchievePassView = (TextView) findViewById(R.id.txt_main_achievement);
        if (mCurrentAchievePassView != null) {
            mCurrentAchievePassView.setText(
                    "您已成功击败"
                            + 100 * mCurrentStageIndex / (Const.SONG_INFQ.length - 1)
                            + "%的玩家");
        }

        // 当前关的索引
        mCurrentStagePassView = (TextView) findViewById(R.id.tv_win_stage);
        if (mCurrentStagePassView != null) {
            mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
        }

        //显示歌曲名称
        mCurrentSongNamePassView = (TextView) findViewById(R.id.tv_win_songname);
        if (mCurrentSongNamePassView != null) {
            mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
        }

        mCurrentCoins += 3;

        // 下一关按键处理
        ImageButton btnPass = (ImageButton) findViewById(
                R.id.btn_win_next_stage);
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (judgeAppPassed()) {
                    // 通关界面
                    Util.startActivity(MainActivity.this, AppPassView.class);

                } else {
                    // 下一关
                    mPassView.setVisibility(View.GONE);

                    // 加载关卡数据
                    initCurrentStageData();
                }
            }
        });
    }

    /**
     * 判断是否通关
     */
    private boolean judgeAppPassed() {
        return (mCurrentStageIndex == Const.SONG_INFQ.length - 1);
    }

    /**
     * increase or decrease
     * true / false
     */
    private boolean handleCoins(int data) {
        // judge current coin number
        if (mCurrentCoins + data >= 0) {
            mCurrentCoins += data;
            mViewCurrentCoins.setText(mCurrentCoins + "");
            return true;
        } else {
            // coin not enough
            return false;
        }
    }

    /**
     * read delete coins from config file
     */
    private int getDeleteWordCoins() {
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }

    /**
     * read tip coins from config file
     */
    private int getTipCoins() {
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    /**
     * handle delete word
     */
    private void handleDeleteWord() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(ID_DIALOG_DELETE_WORD);
                //deleteOneWord();
            }
        });
    }

    /**
     * handle tip word
     */
    private void handleTipWord() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_mention);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(ID_DIALOG_TIP_ANSWER);
                // tipAnswer();
            }
        });
    }

    /**
     * 提示答案
     */
    private void tipAnswer() {
        // coin
        if (!handleCoins(-getTipCoins())) {
            // 金币数量不够，显示对话框
            showConfirmDialog(ID_DIALOG_LACK_COINS);
            return;
        }

        boolean tipWord = false;
        for (int i = 0; i < mBtnSelectWords.size(); i++) {
            MyLog.d(TAG, "mBtnSelectWords" + mBtnSelectWords.size());
            if (mBtnSelectWords.get(i).mWordString.length() == 0) {
                // 根据当前的答案框条件选择对应的文字并填入
                onWordButtonClick(findIsAnswerWord(i));
                tipWord = true;
                break;
            }
        }
        // 没有找到可以填充的答案
        if (!tipWord) {
            sparkWords();
        }
    }

    /**
     * delete words
     */
    private void deleteOneWord() {
        // decrease coin
        if (!handleCoins(-getDeleteWordCoins())) {
            // coin not enough
            showConfirmDialog(ID_DIALOG_LACK_COINS);
            return;
        }
        // delete wordbutton invisiable
        setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
    }

    /**
     * find a word not answer
     */
    private WordButton findNotAnswerWord() {
        Random random = new Random();

        WordButton buf = null;
        while(true) {
            int index = random.nextInt(MyGridView.COUNTS_WORDS);

            buf = mAllWords.get(index);
            MyLog.d(TAG, "Line632 index:" + index + "   buf.mWordString  " + buf.mWordString);

            if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
                return buf;

            }
        }
    }

    /**
     * find a word is answer
     * @param index 当前需要填入答案框的索引
     */
    private WordButton findIsAnswerWord(int index) {
        WordButton buf = null;

        for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
            buf = mAllWords.get(i);

            MyLog.d(TAG, "" + index);
            if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
                return buf;
            }
        }
        return null;
    }

    /**
     * judge is answer
     */
    private boolean isTheAnswerWord(WordButton word) {
        boolean result = false;

        MyLog.d(TAG, "Line665   :mCurrentSong   :" + mCurrentSong.getNameLength() + ";");

        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            if (word.mWordString.
                    equals("" + mCurrentSong.getNameCharacters()[i])) {

                MyLog.d(TAG, "Line667   :" + mCurrentSong.getNameCharacters()[i] + "    i: " + i);
                MyLog.d(TAG, "Line668   :" + word.mWordString);

                result = true;
                break;
            }
        }
        return result;
    }

    /***********************************************
     *
     * 自定义AlertDialog事件响应
     *
     ************************************************/

    /**
     * 答案删除
     */
    private IAlertDialogButtonListener mBtnOkDeleteWordListener =
            new IAlertDialogButtonListener() {

                @Override
                public void onClick() {
                    // 执行事件
                    deleteOneWord();
                }
            };


    /**
     * 答案提示
     */
    private IAlertDialogButtonListener mBtnOkTipAnswerListener =
            new IAlertDialogButtonListener() {

                @Override
                public void onClick() {
                    // 执行事件
                    tipAnswer();

                }
            };


    /**
     * 金币不足
     */
    private IAlertDialogButtonListener mBtnOkLackCoinsListener =
            new IAlertDialogButtonListener() {

                @Override
                public void onClick() {
                    // 执行事件

                }
            };

    /**
     * 显示对话框
     */
    private void showConfirmDialog(int id) {
        switch (id) {
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this,
                        "确认花掉" + getDeleteWordCoins() + "个金币去掉一个错误答案",
                        mBtnOkDeleteWordListener);
                break;

            case ID_DIALOG_TIP_ANSWER:
                Util.showDialog(MainActivity.this,
                        "确认花掉" + getTipCoins() + "个金币获得一个文字提示",
                        mBtnOkTipAnswerListener);
                break;

            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this,
                        "金币不足，去商店补充？",
                        mBtnOkLackCoinsListener);
                break;
        }
    }


    /**
     *
     */


}
