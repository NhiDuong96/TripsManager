package com.android.cndd.tripsmanager.ViewHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.android.cndd.tripsmanager.R;

/**
 * Created by Minh Nhi on 3/12/2018.
 */

public class BackgroundContainer extends FrameLayout {
    boolean mShowing = false;
    Drawable mShadowedBackgroud;
    int mOpenAreaTop, mOpenAreaBottom, mOpenAreaHeight;
    boolean mUpdateBounds = false;


    public BackgroundContainer(Context context) {
        super(context);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        mShadowedBackgroud = getContext().getDrawable(R.drawable.shadowed_background);
    }

    public void showBackground(int top, int bottom) {
        setWillNotDraw(false);
        mOpenAreaTop = top;
        mOpenAreaHeight = bottom;
        mShowing = true;
        mUpdateBounds = true;
    }

    public void hideBackground() {
        setWillNotDraw(true);
        mShowing = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mShowing){
            if(mUpdateBounds){
                mShadowedBackgroud.setBounds(0,0,getWidth(),mOpenAreaHeight);
            }
            canvas.save();
            canvas.translate(0, mOpenAreaTop);
            mShadowedBackgroud.draw(canvas);
            canvas.restore();
        }
    }
}
