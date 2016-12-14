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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.lineageos.recorder.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SoundFragment extends Fragment {

    private TextView mTimerText;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    public SoundFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer,
                             Bundle mSavedInstance) {
        View mView = mInflater.inflate(R.layout.fragment_sound, mContainer, false);

        mTimerText = (TextView) mView.findViewById(R.id.sound_timer);

        return mView;
    }

    public void setTimer(int mTime) {
        if (mTimerText != null) {
            mTimerText.setText(mDateFormat.format(mTime * 1000));
        }
    }

}
