package com.nir.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;

    private List<String> mTracks;

    private int mTrackIdx = 0;

    private final IBinder mPlaybackBinder = new PlaybackBinder();

    private ArrayList<PlayBackListener> mListeners = new ArrayList<>();


    public PlaybackService() {
    }

    public void setTracks(List<String> Tracks) {
        this.mTracks = Tracks;
        mMediaPlayer.stop();
        mTrackIdx = 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mTracks = new ArrayList<>();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mPlaybackBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        for (PlayBackListener listener : mListeners) {
            listener.onMediaPlayerPrepared();
        }
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        NextTrack();
    }

    public class PlaybackBinder extends Binder {
        PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    public void Play(int trackIdx) {
        mMediaPlayer.reset();
        mTrackIdx = trackIdx;
        try {
            mMediaPlayer.setDataSource(mTracks.get(mTrackIdx));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Pause() {
        mMediaPlayer.pause();
    }

    public void Unpause() {
        mMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }

    public void PreviousTrack() {
        notifyUnprepared();
        mMediaPlayer.reset();
        mTrackIdx--;
        if (mTrackIdx < 0)
            mTrackIdx = mTracks.size() - 1;
        mTrackIdx %= mTracks.size();
        try {
            mMediaPlayer.setDataSource(mTracks.get(mTrackIdx));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void NextTrack() {
        notifyUnprepared();
        mMediaPlayer.reset();
        mTrackIdx++;
        mTrackIdx %= mTracks.size();
        try {
            mMediaPlayer.setDataSource(mTracks.get(mTrackIdx));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentTrackDuration(){
        return mMediaPlayer.getDuration();
    }

    public int getTimeElapsed() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void AddListener(PlayBackListener listener) {
        mListeners.add(listener);
    }

    public void RemoveListener(PlayBackListener listener) {
        mListeners.remove(listener);
    }

    public interface PlayBackListener {
        void onMediaPlayerPrepared();
        void onMediaPlayerUnprepared();
    }

    private void notifyUnprepared() {
        for (PlayBackListener listener : mListeners) {
            listener.onMediaPlayerUnprepared();
        }
    }

    public int getTrackIdx(){
        return mTrackIdx;
    }

    public void Seek(int position) {
        mMediaPlayer.seekTo(position);
    }


}
