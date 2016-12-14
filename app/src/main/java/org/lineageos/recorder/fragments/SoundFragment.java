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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.lineageos.recorder.R;
import org.lineageos.recorder.RecorderActivity;
import org.lineageos.recorder.sounds.SoundVisualizer;
import org.lineageos.recorder.utils.Utils;

public class SoundFragment extends Fragment {

    private CardView mStopCard;
    private SoundVisualizer mVisualizer;

    public SoundFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer,
                             Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fragment_sound, mContainer, false);

        mStopCard = (CardView) mView.findViewById(R.id.sound_card_recording);
        mVisualizer = (SoundVisualizer) mView.findViewById(R.id.sound_visualizer);
        Button mStopButton = (Button) mView.findViewById(R.id.sound_recording_button);

        mStopButton.setOnClickListener(mButtonView ->
                ((RecorderActivity) getActivity()).toggleSoundRecorder());
        refresh(getContext());
        return mView;
    }

    // Pass context to avoid unexpected NPE when refreshing from RecorderActivity
    public void refresh(Context mContext) {
        if (mStopCard == null) {
            return;
        }

        mStopCard.setVisibility(Utils.isSoundRecording(mContext) ? View.VISIBLE : View.GONE);
    }

    public SoundVisualizer getVisualizer() {
        return mVisualizer;
    }


}
