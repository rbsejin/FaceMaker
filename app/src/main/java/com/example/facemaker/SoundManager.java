package com.example.facemaker;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    private SoundManager instance;

    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    private Context mContext;

    public SoundManager(Context mContext, SoundPool mSoundPool) {
        this.mContext = mContext;
        this.mSoundPool = mSoundPool;
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    //효과음 추가
    public void addSound(int index, int soundId) {
        mSoundPoolMap.put(index, mSoundPool.load(mContext, soundId, 1));
    }

    //효과음 재생
    public int playSound(int index) {
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }

    //효과음 정지
    public void stopSound(int streamId) {
        mSoundPool.stop(streamId);
    }

    //효과음 일시정지
    public void pauseSound(int streamId) {
        mSoundPool.pause(streamId);
    }

    //효과음 재시작
    public void resumeSound(int streamId) {
        mSoundPool.resume(streamId);
    }
}
