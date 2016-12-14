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
package org.lineageos.recorder.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.lineageos.recorder.R;

public class ScreenFragment extends Fragment {
    private static final int REQUEST_AUDIO_PERMS = 654;

    private Activity mActivity;

    private RelativeLayout mWarningLayout;
    private RelativeLayout mToggleLayout;
    private Button mRequestButton;
    private Switch mAudioSwitch;

    private boolean hasAudioPermission;

    public ScreenFragment() {}

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer,
                             Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fragment_screen, mContainer, false);

        mWarningLayout = (RelativeLayout) mView.findViewById(R.id.screen_warning_layout);
        mToggleLayout = (RelativeLayout) mView.findViewById(R.id.screen_toggle_layout);
        mRequestButton = (Button) mView.findViewById(R.id.screen_request_button);
        mAudioSwitch = (Switch) mView.findViewById(R.id.screen_toggle_audio);

        mActivity = getActivity();

        mAudioSwitch.setOnCheckedChangeListener((mButton, mStatus) ->
                mAudioSwitch.setText(getString(mStatus ?
                        R.string.screen_audio_message_on : R.string.screen_audio_message_off)));

        refresh();

        return mView;
    }

    @Override
    public void onRequestPermissionsResult(int mCode, @NonNull  String[] mPerms,
                                           @NonNull  int[] mResults) {
        if (mCode == REQUEST_AUDIO_PERMS) {
            for (int mRes : mResults) {
                hasAudioPermission &= (mRes == PackageManager.PERMISSION_GRANTED);
            }

            refresh();
        }
    }

    private void refresh() {
        hasAudioPermission = hasPermission();

        mToggleLayout.setVisibility(hasAudioPermission ? View.VISIBLE : View.GONE);
        mWarningLayout.setVisibility(hasAudioPermission ? View.GONE : View.VISIBLE);

        mRequestButton.setOnClickListener(view -> {
            String mPerms[] = { Manifest.permission.RECORD_AUDIO };
            requestPermissions(mPerms, REQUEST_AUDIO_PERMS);
        });
    }

    private boolean hasPermission() {
        int mRes = mActivity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        return mRes == PackageManager.PERMISSION_GRANTED;
    }

    public boolean withAudio() {
        return mAudioSwitch.isChecked();
    }
}
