/*
 * Copyright (C) 2016 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.recorder;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.lineageos.recorder.screen.ScreencastService;
import org.lineageos.recorder.sounds.RecorderBinder;
import org.lineageos.recorder.sounds.SoundRecorderService;

public class RecorderActivity extends AppCompatActivity {
    private enum UiStatus {
        NOTHING,
        SOUND,
        SCREEN
    }

    private static final int REQUEST_STORAGE_PERMS = 439;
    private static final int REQUEST_AUDIO_PERMS = 440;

    private FloatingActionButton mSoundFab;
    private FloatingActionButton mScreenFab;
    private LinearLayout mRootLayout;
    private LinearLayout mSoundLayout;
    private LinearLayout mScreenLayout;

    private ServiceConnection mConnection;
    private SoundRecorderService mSoundService;

    private SharedPreferences mPrefs;


    int mScreenPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        startService(new Intent(this, SoundRecorderService.class));
        setupConnection();

        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        mSoundFab = (FloatingActionButton) findViewById(R.id.sound_fab);
        mScreenFab = (FloatingActionButton) findViewById(R.id.screen_fab);
        mSoundLayout = (LinearLayout) findViewById(R.id.sound_root_layout);
        mScreenLayout = (LinearLayout) findViewById(R.id.screen_root_layout);

        mPrefs = getSharedPreferences(ScreencastService.PREFS, 0);

        mSoundFab.setOnClickListener(v -> {
            if (!hasStoragePermission()) {
                String mPerms[] = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                requestPermissions(mPerms, REQUEST_STORAGE_PERMS);
                return;
            }
            toggleSoundRecorder();
        });

        mScreenFab.setOnClickListener(v -> {
            if (!hasStoragePermission()) {
                String mPerms[] = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
                requestPermissions(mPerms, REQUEST_STORAGE_PERMS);
                return;
            }
            toggleScreenRecorder();
        });

        switch (mPrefs.getString(ScreencastService.KEY_RECORDING, "")) {
            case ScreencastService.PREF_RECORDING_NOTHING:
                setRecordingState(UiStatus.NOTHING);
                break;
            case ScreencastService.PREF_RECORDING_SOUND:
                setRecordingState(UiStatus.SOUND);
                break;
            case ScreencastService.PREF_RECORDING_SCREEN:
                setRecordingState(UiStatus.SCREEN);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int mCode, @NonNull String[] mPerms,
                                           @NonNull  int[] mResults) {
        switch (mCode) {
            case REQUEST_AUDIO_PERMS:
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED) {
                    fabClicked();
                }
                break;
            case REQUEST_STORAGE_PERMS:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    fabClicked();
                }
                break;
        }
    }

    private void setupConnection() {
        if (!hasAudioPermission()) {
            String mPerms[] = { Manifest.permission.RECORD_AUDIO };
            requestPermissions(mPerms, REQUEST_AUDIO_PERMS);
            return;
        }


        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mSoundService = ((RecorderBinder) iBinder).getService();
                if (!mSoundService.isRecording()) {
                    mSoundService.startRecording();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mSoundService = null;
            }
        };
    }

    private void fabClicked() {

        switch (mScreenPosition) {
            case 0:
                break;
            case 1:
                break;
        }
    }

    private void toggleSoundRecorder() {
        if (mSoundService == null) {
            bindService(new Intent(this, SoundRecorderService.class), mConnection,
                    BIND_AUTO_CREATE);
            setRecordingState(UiStatus.SOUND);
        } else if (mSoundService.isRecording()) {
            mSoundService.stopRecording();
            setRecordingState(UiStatus.NOTHING);
        } else {
            mSoundService.startRecording();
            setRecordingState(UiStatus.SOUND);
        }
    }

    private void toggleScreenRecorder() {

        if (ScreencastService.PREF_RECORDING_SCREEN.equals(mPrefs.getString(
                ScreencastService.KEY_RECORDING, ""))) {
            // Stop
            startService(new Intent(ScreencastService.ACTION_STOP_SCREENCAST)
                    .setClass(this, ScreencastService.class));
            setRecordingState(UiStatus.NOTHING);
        } else {
            // Start
            new Handler().postDelayed(() -> {
                Intent mIntent = new Intent(ScreencastService.ACTION_START_SCREENCAST);
                mIntent.putExtra(ScreencastService.EXTRA_WITHAUDIO, false);
                startService(mIntent.setClass(this, ScreencastService.class));
                finish();
            }, 1000);
            setRecordingState(UiStatus.SCREEN);
        }
    }

    private void setRecordingState(UiStatus mStatus) {
        switch (mStatus) {
            case NOTHING:
                mPrefs.edit().putString(ScreencastService.KEY_RECORDING,
                        ScreencastService.PREF_RECORDING_NOTHING).apply();
                mSoundLayout.setVisibility(View.VISIBLE);
                mScreenLayout.setVisibility(View.VISIBLE);
                mRootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.soundColor));
                break;
            case SOUND:
                mPrefs.edit().putString(ScreencastService.KEY_RECORDING,
                        ScreencastService.PREF_RECORDING_SOUND).apply();
                mScreenLayout.setVisibility(View.GONE);
                mRootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.soundColor));
                break;
            case SCREEN:
                mPrefs.edit().putString(ScreencastService.KEY_RECORDING,
                        ScreencastService.PREF_RECORDING_SCREEN).apply();
                mSoundLayout.setVisibility(View.GONE);
                mRootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.screenColor));
                break;
        }

    }

    private boolean hasStoragePermission() {
        int mRes = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return mRes == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAudioPermission() {
        int mRes = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        return mRes == PackageManager.PERMISSION_GRANTED;
    }

}
