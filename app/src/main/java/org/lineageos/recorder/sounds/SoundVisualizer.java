package org.lineageos.recorder.sounds;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.lineageos.recorder.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizer View, based on https://github.com/FireZenk/AudioWaves
 */
public class SoundVisualizer extends LinearLayout implements OnAudioLevelUpdatedListener {
    private static final int LAYOUT_WIDTH = 20;
    private static final int LAYOUT_HEIGHT = 20;
    private static final int LAYOUT_MARGIN = 10;
    private static final int WAVE_CORNER_RADIUS = 50;

    private Context mContext;

    private LinearLayout.LayoutParams mParams;

    private List<View> mWaveList = new ArrayList<>();

    private int mHeight = 200;
    private int mWidth = 100;
    private int mGravity = 0;

    public SoundVisualizer(Context mContext) {
        super(mContext);
        this.mContext = mContext;

        setup();
    }

    public SoundVisualizer(Context mContext, AttributeSet mAttrSet) {
        super(mContext, mAttrSet);
        this.mContext = mContext;

        if (isInEditMode()) {
            setAttrs(mAttrSet);
        }
        setup();
    }

    public SoundVisualizer(Context mContext, AttributeSet mAttrSet, int mDefStyleAttr) {
        super(mContext, mAttrSet, mDefStyleAttr);
        this.mContext = mContext;

        if (isInEditMode()) {
            setAttrs(mAttrSet);
        }
        setup();
    }

    private void setAttrs(AttributeSet mAttrSet) {
        TypedArray mArray = mContext.getTheme().obtainStyledAttributes(mAttrSet,
                R.styleable.visualizer_style, 0, 0);

        try {
            mHeight = mArray.getInteger(R.styleable.visualizer_style_visualizer_height,
                    LAYOUT_HEIGHT);
            mWidth = mArray.getInteger(R.styleable.visualizer_style_visualizer_width,
                    LAYOUT_WIDTH);
            mGravity = mArray.getInteger(R.styleable.visualizer_style_visualizer_gravity,
                    mGravity);
        } finally {
            mArray.recycle();
        }
    }

    private void setup() {
        // Setup layout params
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        switch (mGravity) {
            case 0:
                setGravity(Gravity.CENTER);
                break;
            case 1:
                setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
                break;
            case 2:
                setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                break;
            case 3:
                setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
                break;
            case 4:
                setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                break;
        }

        mParams = new LinearLayout.LayoutParams(mWidth, mHeight);
        mParams.setMargins(LAYOUT_MARGIN, 0, LAYOUT_MARGIN, 0);

        // Draw waves
        for (int mCounter = 0; mCounter < 5; mCounter++) {
            View mView = new View(mContext);
            mView.setLayoutParams(mParams);
            GradientDrawable mDrawable = new GradientDrawable();
            mDrawable.setColor(ContextCompat.getColor(mContext, R.color.white));
            mDrawable.setCornerRadius(WAVE_CORNER_RADIUS);
            mView.setBackground(mDrawable);
            mWaveList.add(mView);
            addView(mView);
        }
    }

    @Override
    public void onAudioLevelUpdated(int mValue) {
       // Log.d("OHAI", String.format("Value: %1$d", mValue));
        for (int mCounter = 0; mCounter < 5 && mCounter < mWaveList.size(); mCounter++) {
            int mRand = new SecureRandom().nextInt(9) + 1;
            int mSize = mValue / mRand;

            Log.d("OHAI", String.format("Wave %1$d : %2$d", mCounter, mSize));

            mParams = new LinearLayout.LayoutParams(mWidth,
                    mSize < LAYOUT_HEIGHT ? LAYOUT_HEIGHT : mSize);
            mParams.setMargins(LAYOUT_MARGIN, 0, LAYOUT_MARGIN, 0);
            mWaveList.get(mCounter).setLayoutParams(mParams);
        }
    }
}