/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jay.android.pages.audio;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jay.android.R;
import com.jay.android.base.BaseActivity;
import com.jay.android.player.MediaPlayerHolder;
import com.jay.android.player.PlaybackInfoListener;
import com.jay.android.player.PlayerAdapter;

/**
 * Allows playback of a single MP3 file via the UI. It contains a {@link MediaPlayerHolder}
 * which implements the {@link PlayerAdapter} interface that the activity uses to control
 * audio playback.
 */
public final class AudioPlayActivity extends BaseActivity {

    public static final String TAG = AudioPlayActivity.class.getSimpleName();

    public static final String AUDIO_PATH = "AUDIO_PATH";
    public static final String AUDIO_TITLE = "AUDIO_TITLE";

    private SeekBar mSeekbarAudio;
    private ImageView mPlayOrPauseButton;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;

    private TextView tvDuration;

    private float speedRate = 1.0f;
    private int speedIndex = 2;
    private float[] speedSet = {0.5f, 0.8f, 1.0f, 1.2f, 1.6f,2.0f};
    private String[] speedSetDisplay = {"0.5x", "0.8x", "1.0x", "1.2x", "1.6x","2.0x"};

    private float timeDivider = 1.0f;
    private int timeIndex = 2;
    private int[] timeSet = {1000, 3000, 5000, 7000, 10000,15000};
    private String[] timeSetDisplay = {"1s", "3s", "5s", "7s", "10s","15s"};


    @Override
    public int getContentViewId() {
        return R.layout.activity_audio_play;
    }

    @Override
    public String getPageTitle() {
        return getIntent().getStringExtra(AUDIO_TITLE);
    }

    @Override
    public boolean isShowFab() {
        return false;
    }

    @Override
    public void initView() {
        initializeUI();
        initializeSeekbar();
        initializePlaybackController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            mPlayerAdapter.loadMedia(getIntent().getStringExtra(AUDIO_PATH));
            Log.d(TAG, "onStart: create MediaPlayer");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isChangingConfigurations() && mPlayerAdapter.isPlaying()) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing");
        } else {
            mPlayerAdapter.release();
            Log.d(TAG, "onStop: release MediaPlayer");
        }
    }

    private void initializeUI() {

        ImageView mBackButton = findViewById(R.id.button_back);
        ImageView mSkipButton = findViewById(R.id.button_skip);
        mPlayOrPauseButton = findViewById(R.id.button_play_pause);


        mSeekbarAudio = findViewById(R.id.seekbar_audio);
        tvDuration = findViewById(R.id.tv_duration);

        mBackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int progress = mSeekbarAudio.getProgress();
                        if (progress > timeSet[timeIndex]) {
                            mSeekbarAudio.setProgress(progress - timeSet[timeIndex]);
                        } else {
                            mSeekbarAudio.setProgress(0);
                        }
                        mPlayerAdapter.goBack(timeSet[timeIndex]);
                    }
                });

        mSkipButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int progress = mSeekbarAudio.getProgress();
                        if (progress + timeSet[timeIndex] < mSeekbarAudio.getMax()) {
                            mSeekbarAudio.setProgress(progress + timeSet[timeIndex]);
                        } else {
                            mSeekbarAudio.setProgress(mSeekbarAudio.getMax());
                        }
                        mPlayerAdapter.skip(timeSet[timeIndex]);
                    }
                });
        mPlayOrPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayerAdapter.isPlaying()) {
//                            mPlayOrPauseButton.setText("Play");
                            mPlayOrPauseButton.setImageResource(R.drawable.ic_play);
                            mPlayerAdapter.pause();
                        } else {
                            mPlayOrPauseButton.setImageResource(R.drawable.ic_pause);
                            mPlayerAdapter.play();
                        }
                    }
                });

        final TextView time = findViewById(R.id.tv_divider_time);
        ImageView ivTimeMinus = findViewById(R.id.iv_time_left);
        ImageView ivTimeAdd = findViewById(R.id.iv_time_right);

        final TextView speed = findViewById(R.id.tv_speed);
        ImageView ivSpeedMinus = findViewById(R.id.iv_speed_left);
        ImageView ivSpeedAdd = findViewById(R.id.iv_speed_right);

        ivTimeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeIndex <= 0) {
                    timeIndex = 0;
                } else {
                    timeIndex--;
                }
                time.setText(timeSetDisplay[timeIndex]);
            }
        });

        ivTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeIndex >= timeSet.length - 1) {
                    timeIndex = timeSet.length - 1;
                } else {
                    timeIndex++;
                }
                time.setText(timeSetDisplay[timeIndex]);
            }
        });

        ivSpeedMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speedIndex <= 0) {
                    speedIndex = 0;
                } else {
                    speedIndex--;
                }
                speed.setText(speedSetDisplay[speedIndex]);
                mPlayerAdapter.setSpeedRate(speedSet[speedIndex]);
            }
        });

        ivSpeedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speedIndex >= speedSet.length - 1) {
                    speedIndex = speedSet.length - 1;
                } else {
                    speedIndex++;
                }
                speed.setText(speedSetDisplay[speedIndex]);
                mPlayerAdapter.setSpeedRate(speedSet[speedIndex]);
            }
        });
    }


    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
        Log.d(TAG, "initializePlaybackController: created MediaPlayerHolder");
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
        Log.d(TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    private void updateDuration(int duration) {
        int min = duration / 60000;
        int sec = duration % 60000;
        sec /= 1000;
        tvDuration.setText(completedByZero(min)+":"+completedByZero(sec));
    }

    private String completedByZero(int n){
        if(n<10){
            return "0"+n;
        }else {
            return String.valueOf(n);
        }
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
            updateDuration(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekbarAudio.setProgress(position);
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format("onStateChanged(%s)", stateToString));
        }

        @Override
        public void onPlaybackCompleted() {
            mPlayOrPauseButton.setImageResource(R.drawable.ic_play);
        }

        @Override
        public void onLogUpdated(String message) {
            log(message);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    }
}
