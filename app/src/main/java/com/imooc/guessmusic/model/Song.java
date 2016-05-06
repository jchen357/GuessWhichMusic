package com.imooc.guessmusic.model;

/**
 * Created by Administrator on 2016/5/4 0004.
 */

public class Song {
    // 歌曲名称
    private String mSongName;

    // 歌曲的文件名
    private String mSongFileName;

    // 歌曲名字长度
    private int mNameLength;

    public char[] getNameCharacters() {
        return mSongName.toCharArray();
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
        
        this.mNameLength = songName.length();
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String songFileName) {
        this.mSongFileName = songFileName;
    }

    public int getNameLength() {
        return mNameLength;
    }

    public void setNameLenggth(int nameLength) {
        this.mNameLength = nameLength;
    }
}
