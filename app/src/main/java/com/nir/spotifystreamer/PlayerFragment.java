package com.nir.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment implements PlaybackService.PlayBackListener {


    private ImageView mImage;
    private TextView mArtistName;
    private TextView mAlbumName;
    private TextView mTrackName;
    private TextView mTrackDuration;
    private TextView mTimeElapsed;
    private ImageButton mPlayButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private SeekBar mSeekBar;

    private final int BUTTON_MODE_PLAY = 0;
    private final int BUTTON_MODE_PAUSE = 1;

    private int mTrackIdx;

    private boolean mUpdateUi;



    private List<SpotifyArrayAdapter.DataEntity> mTracks;

    private PlaybackService mPlaybackService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlaybackService.PlaybackBinder binder = (PlaybackService.PlaybackBinder)iBinder;
            mPlaybackService = binder.getService();
            mPlaybackService.AddListener(PlayerFragment.this);
            mPlaybackService.setTracks(PlayerFragment.this.createMediaURLs());
            mPlaybackService.Play(mTrackIdx);




        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public PlayerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mImage = (ImageView)rootView.findViewById(R.id.player_album_image_imageview);
        mArtistName = (TextView)rootView.findViewById(R.id.player_artist_name_textview);
        mAlbumName = (TextView)rootView.findViewById(R.id.player_album_name_textview);
        mTrackName = (TextView)rootView.findViewById(R.id.player_track_name_textview);
        mTrackDuration = (TextView)rootView.findViewById(R.id.player_track_duration_textview);
        mTimeElapsed = (TextView)rootView.findViewById(R.id.player_time_elapsed_textview);
        mPlayButton = (ImageButton)rootView.findViewById(R.id.player_play_button);
        mNextButton = (ImageButton)rootView.findViewById(R.id.player_next_track_button);
        mPrevButton = (ImageButton)rootView.findViewById(R.id.player_prev_track_button);
        mSeekBar = (SeekBar)rootView.findViewById(R.id.player_seekbar);

        mPlayButton.setTag(BUTTON_MODE_PAUSE);
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);


        Intent intent = getActivity().getIntent();


        if (intent != null) {
            mTrackIdx = intent.getIntExtra(getString(R.string.track_idx),0);
            String artistName = intent.getStringExtra(getString(R.string.artist_name));
            mArtistName.setText(artistName);
            //new FetchTrackClass().execute(mTrackId);
        }
        mTracks = Spotify.getInstance().getLastQuery();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int buttonMode = (int)mPlayButton.getTag();
                if (buttonMode == BUTTON_MODE_PAUSE) {
                    mPlaybackService.Pause();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                    mPlayButton.setTag(BUTTON_MODE_PLAY);
                }
                else {
                    mPlaybackService.Unpause();
                    mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
                    mPlayButton.setTag(BUTTON_MODE_PAUSE);
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlaybackService.NextTrack();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlaybackService.PreviousTrack();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percentage = seekBar.getProgress() / 100f;
                float newPosition = percentage * mPlaybackService.getCurrentTrackDuration();
                mPlaybackService.Seek((int)newPosition);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent playIntent = new Intent(getActivity(),PlaybackService.class);
        getActivity().bindService(playIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(playIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUpdateUi = true;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        mUpdateUi = false;
        mPlaybackService.RemoveListener(this);
        getActivity().unbindService(mServiceConnection);
        super.onStop();
    }

    // Based on a StackOverflow code snippet
    private String formatTime(long time) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    @Override
    public void onMediaPlayerPrepared() {
        mUpdateUi = true;
        mTrackDuration.setText(formatTime(mPlaybackService.getCurrentTrackDuration()));
        int trackIdx = mPlaybackService.getTrackIdx();
        SpotifyArrayAdapter.DataEntity track = mTracks.get(trackIdx);
        mAlbumName.setText(track.mText2);
        mTrackName.setText(track.mText1);
        Picasso.with(getActivity()).load(track.mTmageUrl).into(mImage);
        mTimeElapsed.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUpdateUi) {
                    int timeElapsed = mPlaybackService.getTimeElapsed();
                    float seekPosition = 100 * timeElapsed / mPlaybackService.getCurrentTrackDuration();
                    mTimeElapsed.setText(formatTime(timeElapsed));
                    mTimeElapsed.postDelayed(this, 200);
                    mSeekBar.setProgress((int) seekPosition);
                }
            }
        }, 200);
    }

    @Override
    public void onMediaPlayerUnprepared() {
        mUpdateUi = false;
    }



    private List<String> createMediaURLs() {
        List<String> urls = new ArrayList<>();
        if (mTracks != null) {
            for(SpotifyArrayAdapter.DataEntity track : mTracks) {
                urls.add(track.mDataUrl);
            }
        }
        return urls;
    }

}
