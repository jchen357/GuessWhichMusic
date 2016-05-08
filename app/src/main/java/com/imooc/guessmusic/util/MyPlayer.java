package com.imooc.guessmusic.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音乐播放类
 *
 * Created by Administrator on 2016/5/8 0008.
 *
 */
public class MyPlayer {

    // 索引
    public final static int INDEX_STONE_ENTER = 0;
    public final static int INDEX_STONE_CANCEL = 1;
    public final static int INDEX_STONE_COIN = 2;

    // 音效的文件名称
    private final static String[] SONG_NAMES =
            {"enter.mp3", "cancel.mp3", "coin.mp3"};

    // 音效
    private static  MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];

    private static String TAG = "MainActivity";

    // 歌曲播放
    private static MediaPlayer mMusicMediaPlayer;

    public static void playTone(Context context, int index) {
        // 加载声音
        AssetManager assetManager = context.getAssets();

        if (mToneMediaPlayer[index] == null) {
            mToneMediaPlayer[index] = new MediaPlayer();

            try {
                AssetFileDescriptor fileDescriptor = assetManager.openFd(SONG_NAMES[index]);
                mToneMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());

                mToneMediaPlayer[index].prepare();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mToneMediaPlayer[index].start();

    }

    public static void playSong(Context context, String fileName) {

        if (mMusicMediaPlayer == null) {

            mMusicMediaPlayer = new MediaPlayer();
        }

        MyLog.d(TAG, "LINE29    " + fileName);
        // 强制重置 针对非第一次播放
        mMusicMediaPlayer.reset();

        // 加载声音
        AssetManager assetManager = context.getAssets();
        try {
            MyLog.d(TAG, "LINE36    try   ");

            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
            MyLog.d(TAG, "LINE39    try   ");
            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());

            //fileDescriptor.close();

            mMusicMediaPlayer.prepare();

            // 声音播放
            mMusicMediaPlayer.start();

        } catch (IOException e) {
            MyLog.d(TAG, "LINE46    catch   " + "mMusicMediaPlayer.start()");

            e.printStackTrace();
        }

    }

    public static void stopTheSong (Context context) {
        if (mMusicMediaPlayer != null) {
            mMusicMediaPlayer.stop();
        }
    }

}
