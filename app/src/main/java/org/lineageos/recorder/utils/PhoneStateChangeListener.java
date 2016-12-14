package org.lineageos.recorder.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.lineageos.recorder.RecorderActivity;

public class PhoneStateChangeListener extends PhoneStateListener {
    private int mOldCallState = TelephonyManager.CALL_STATE_IDLE;

    private Context mContext;

    public PhoneStateChangeListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCallStateChanged(int mState, String mIncomingNumber) {
        switch (mState) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (mOldCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    mOldCallState = mState;
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (mOldCallState == TelephonyManager.CALL_STATE_IDLE) {
                    mOldCallState = mState;

                    // Stop sound recorder
                    ((RecorderActivity) mContext).toggleSoundRecorder();
                }
                break;
        }
    }
}
